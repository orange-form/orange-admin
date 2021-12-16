package com.orangeforms.common.online.api.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.dto.OnlineFormDto;
import com.orangeforms.common.online.model.*;
import com.orangeforms.common.online.service.*;
import com.orangeforms.common.online.vo.OnlineFormVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在线表单操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineForm")
public class OnlineFormController {

    @Autowired
    private OnlineFormService onlineFormService;
    @Autowired
    private OnlineDatasourceService onlineDatasourceService;
    @Autowired
    private OnlineDatasourceRelationService onlineDatasourceRelationService;
    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlineColumnService onlineColumnService;
    @Autowired
    private OnlineVirtualColumnService onlineVirtualColumnService;
    @Autowired
    private OnlineDictService onlineDictService;
    @Autowired
    private OnlineRuleService onlineRuleService;

    /**
     * 新增在线表单数据。
     *
     * @param onlineFormDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody OnlineFormDto onlineFormDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineFormDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineForm onlineForm = MyModelUtil.copyTo(onlineFormDto, OnlineForm.class);
        // 验证关联Id的数据合法性
        CallResult callResult = onlineFormService.verifyRelatedData(onlineForm, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Set<Long> datasourceIdSet = null;
        if (CollUtil.isNotEmpty(onlineFormDto.getDatasourceIdList())) {
            datasourceIdSet = new HashSet<>(onlineFormDto.getDatasourceIdList());
            if (!onlineDatasourceService.existAllPrimaryKeys(datasourceIdSet)) {
                errorMessage = "数据验证失败，当前在线表单包含不存在的数据源Id！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        onlineForm = onlineFormService.saveNew(onlineForm, datasourceIdSet);
        return ResponseResult.success(onlineForm.getFormId());
    }

    /**
     * 更新在线表单数据。
     *
     * @param onlineFormDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineFormDto onlineFormDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineFormDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineForm onlineForm = MyModelUtil.copyTo(onlineFormDto, OnlineForm.class);
        OnlineForm originalOnlineForm = onlineFormService.getById(onlineForm.getFormId());
        if (originalOnlineForm == null) {
            errorMessage = "数据验证失败，当前在线表单并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = onlineFormService.verifyRelatedData(onlineForm, originalOnlineForm);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Set<Long> datasourceIdSet = null;
        if (CollUtil.isNotEmpty(onlineFormDto.getDatasourceIdList())) {
            datasourceIdSet = new HashSet<>(onlineFormDto.getDatasourceIdList());
            if (!onlineDatasourceService.existAllPrimaryKeys(datasourceIdSet)) {
                errorMessage = "数据验证失败，当前在线表单包含不存在的数据源Id！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        if (!onlineFormService.update(onlineForm, originalOnlineForm, datasourceIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除在线表单数据。
     *
     * @param formId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long formId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(formId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        OnlineForm originalOnlineForm = onlineFormService.getById(formId);
        if (originalOnlineForm == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前在线表单并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineFormService.remove(formId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的在线表单列表。
     *
     * @param onlineFormDtoFilter 过滤对象。
     * @param orderParam          排序参数。
     * @param pageParam           分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineFormVo>> list(
            @MyRequestBody OnlineFormDto onlineFormDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineForm onlineFormFilter = MyModelUtil.copyTo(onlineFormDtoFilter, OnlineForm.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineForm.class);
        List<OnlineForm> onlineFormList =
                onlineFormService.getOnlineFormListWithRelation(onlineFormFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineFormList, OnlineForm.INSTANCE));
    }

    /**
     * 查看指定在线表单对象详情。
     *
     * @param formId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<OnlineFormVo> view(@RequestParam Long formId) {
        if (MyCommonUtil.existBlankArgument(formId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineForm onlineForm = onlineFormService.getByIdWithRelation(formId, MyRelationParam.full());
        if (onlineForm == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineFormVo onlineFormVo = OnlineForm.INSTANCE.fromModel(onlineForm);
        List<OnlineFormDatasource> formDatasourceList = onlineFormService.getFormDatasourceListByFormId(formId);
        if (CollUtil.isNotEmpty(formDatasourceList)) {
            onlineFormVo.setDatasourceIdList(formDatasourceList.stream()
                    .map(OnlineFormDatasource::getDatasourceId).collect(Collectors.toList()));
        }
        return ResponseResult.success(onlineFormVo);
    }

    /**
     * 获取指定在线表单对象在前端渲染时所需的所有数据对象。
     *
     * @param formId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/render")
    public ResponseResult<JSONObject> render(@RequestParam Long formId) {
        if (MyCommonUtil.existBlankArgument(formId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineForm onlineForm = onlineFormService.getByIdWithRelation(formId, MyRelationParam.full());
        if (onlineForm == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineFormVo onlineFormVo = OnlineForm.INSTANCE.fromModel(onlineForm);
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("onlineForm", onlineFormVo);
        List<OnlineFormDatasource> formDatasourceList = onlineFormService.getFormDatasourceListByFormId(formId);
        if (CollUtil.isEmpty(formDatasourceList)) {
            return ResponseResult.success(jsonObject);
        }
        Set<Long> datasourceIdSet = formDatasourceList.stream()
                .map(OnlineFormDatasource::getDatasourceId).collect(Collectors.toSet());
        List<OnlineDatasource> onlineDatasourceList = onlineDatasourceService.getOnlineDatasourceList(datasourceIdSet);
        jsonObject.putOpt("onlineDatasourceList", onlineDatasourceList);
        List<OnlineDatasourceRelation> onlineDatasourceRelationList =
                onlineDatasourceRelationService.getOnlineDatasourceRelationListByDatasourceIds(datasourceIdSet, null);
        if (CollUtil.isNotEmpty(onlineDatasourceRelationList)) {
            jsonObject.putOpt("onlineDatasourceRelationList", onlineDatasourceRelationList);
        }
        List<OnlineDatasourceTable> onlineDatasourceTableList =
                onlineDatasourceService.getOnlineDatasourceTableList(datasourceIdSet);
        if (CollUtil.isNotEmpty(onlineDatasourceTableList)) {
            Set<Long> tableIdSet = onlineDatasourceTableList.stream()
                    .map(OnlineDatasourceTable::getTableId).collect(Collectors.toSet());
            List<OnlineTable> onlineTableList = onlineTableService.getOnlineTableList(tableIdSet);
            jsonObject.putOpt("onlineTableList", onlineTableList);
            List<OnlineColumn> onlineColumnList = onlineColumnService.getOnlineColumnListByTableIds(tableIdSet);
            jsonObject.putOpt("onlineColumnList", onlineColumnList);
            List<OnlineVirtualColumn> virtualColumnList =
                    onlineVirtualColumnService.getOnlineVirtualColumnListByTableIds(tableIdSet);
            jsonObject.putOpt("onlineVirtualColumnList", virtualColumnList);
            Set<Long> dictIdSet = onlineColumnList.stream()
                    .filter(c -> c.getDictId() != null).map(OnlineColumn::getDictId).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(dictIdSet)) {
                List<OnlineDict> onlineDictList = onlineDictService.getOnlineDictList(dictIdSet);
                if (CollUtil.isNotEmpty(onlineDictList)) {
                    jsonObject.putOpt("onlineDictList", onlineDictList);
                }
            }
            Set<Long> columnIdSet = onlineColumnList.stream().map(OnlineColumn::getColumnId).collect(Collectors.toSet());
            List<OnlineColumnRule> colunmRuleList = onlineRuleService.getOnlineColumnRuleListByColumnIds(columnIdSet);
            if (CollUtil.isNotEmpty(colunmRuleList)) {
                jsonObject.putOpt("onlineColumnRuleList", colunmRuleList);
            }
        }
        return ResponseResult.success(jsonObject);
    }

}
