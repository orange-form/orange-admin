package com.orangeforms.common.online.api.controller;

import cn.hutool.core.collection.CollUtil;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.core.validator.AddGroup;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.dto.OnlineDatasourceRelationDto;
import com.orangeforms.common.online.model.*;
import com.orangeforms.common.online.object.SqlTable;
import com.orangeforms.common.online.object.SqlTableColumn;
import com.orangeforms.common.online.service.*;
import com.orangeforms.common.online.vo.OnlineDatasourceRelationVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 数据源关联操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineDatasourceRelation")
public class OnlineDatasourceRelationController {

    @Autowired
    private OnlineDatasourceRelationService onlineDatasourceRelationService;
    @Autowired
    private OnlineDatasourceService onlineDatasourceService;
    @Autowired
    private OnlineVirtualColumnService onlineVirtualColumnService;
    @Autowired
    private OnlineDblinkService onlineDblinkService;
    @Autowired
    private OnlineFormService onlineFormService;

    /**
     * 新增数据关联数据。
     *
     * @param onlineDatasourceRelationDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody OnlineDatasourceRelationDto onlineDatasourceRelationDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(
                onlineDatasourceRelationDto, Default.class, AddGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineDatasourceRelation onlineDatasourceRelation =
                MyModelUtil.copyTo(onlineDatasourceRelationDto, OnlineDatasourceRelation.class);
        OnlineDatasource onlineDatasource =
                onlineDatasourceService.getById(onlineDatasourceRelationDto.getDatasourceId());
        if (onlineDatasource == null) {
            errorMessage = "数据验证失败，关联的数据源Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineDblink onlineDblink = onlineDblinkService.getById(onlineDatasource.getDblinkId());
        SqlTable slaveTable = onlineDblinkService.getDblinkTable(
                onlineDblink, onlineDatasourceRelationDto.getSlaveTableName());
        if (slaveTable == null) {
            errorMessage = "数据验证失败，指定的数据表不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SqlTableColumn slaveColumn = null;
        for (SqlTableColumn column : slaveTable.getColumnList()) {
            if (column.getColumnName().equals(onlineDatasourceRelationDto.getSlaveColumnName())) {
                slaveColumn = column;
                break;
            }
        }
        if (slaveColumn == null) {
            errorMessage = "数据验证失败，指定的数据表字段 [" + onlineDatasourceRelationDto.getSlaveColumnName() + "] 不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult =
                onlineDatasourceRelationService.verifyRelatedData(onlineDatasourceRelation, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        onlineDatasourceRelation = onlineDatasourceRelationService.saveNew(onlineDatasourceRelation, slaveTable, slaveColumn);
        return ResponseResult.success(onlineDatasourceRelation.getRelationId());
    }

    /**
     * 更新数据关联数据。
     *
     * @param onlineDatasourceRelationDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineDatasourceRelationDto onlineDatasourceRelationDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(
                onlineDatasourceRelationDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineDatasourceRelation onlineDatasourceRelation =
                MyModelUtil.copyTo(onlineDatasourceRelationDto, OnlineDatasourceRelation.class);
        OnlineDatasourceRelation originalOnlineDatasourceRelation =
                onlineDatasourceRelationService.getById(onlineDatasourceRelation.getRelationId());
        if (originalOnlineDatasourceRelation == null) {
            errorMessage = "数据验证失败，当前数据源关联并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasourceRelationDto.getRelationType().equals(originalOnlineDatasourceRelation.getRelationType())) {
            errorMessage = "数据验证失败，不能修改关联类型！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasourceRelationDto.getSlaveTableId().equals(originalOnlineDatasourceRelation.getSlaveTableId())) {
            errorMessage = "数据验证失败，不能修改从表Id！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasourceRelationDto.getDatasourceId().equals(originalOnlineDatasourceRelation.getDatasourceId())) {
            errorMessage = "数据验证失败，不能修改数据源Id！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = onlineDatasourceRelationService
                .verifyRelatedData(onlineDatasourceRelation, originalOnlineDatasourceRelation);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!onlineDatasourceRelationService.update(onlineDatasourceRelation, originalOnlineDatasourceRelation)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除数据关联数据。
     *
     * @param relationId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long relationId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(relationId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        OnlineDatasourceRelation originalOnlineDatasourceRelation = onlineDatasourceRelationService.getById(relationId);
        if (originalOnlineDatasourceRelation == null) {
            errorMessage = "数据验证失败，当前数据源关联并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        OnlineVirtualColumn virtualColumnFilter = new OnlineVirtualColumn();
        virtualColumnFilter.setRelationId(relationId);
        List<OnlineVirtualColumn> virtualColumnList =
                onlineVirtualColumnService.getOnlineVirtualColumnList(virtualColumnFilter, null);
        if (CollUtil.isNotEmpty(virtualColumnList)) {
            OnlineVirtualColumn virtualColumn = virtualColumnList.get(0);
            errorMessage = "数据验证失败，数据源关联正在被虚拟字段 [" + virtualColumn.getColumnPrompt() + "] 使用，不能被删除！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        List<OnlineForm> formList =
                onlineFormService.getOnlineFormListByTableId(originalOnlineDatasourceRelation.getSlaveTableId());
        if (CollUtil.isNotEmpty(formList)) {
            errorMessage = "数据验证失败，当前数据源关联正在被 [" + formList.get(0).getFormName() + "] 表单占用，请先删除关联数据！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasourceRelationService.remove(relationId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的数据关联列表。
     *
     * @param onlineDatasourceRelationDtoFilter 过滤对象。
     * @param orderParam                        排序参数。
     * @param pageParam 分                      页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineDatasourceRelationVo>> list(
            @MyRequestBody OnlineDatasourceRelationDto onlineDatasourceRelationDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineDatasourceRelation onlineDatasourceRelationFilter =
                MyModelUtil.copyTo(onlineDatasourceRelationDtoFilter, OnlineDatasourceRelation.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineDatasourceRelation.class);
        List<OnlineDatasourceRelation> onlineDatasourceRelationList =
                onlineDatasourceRelationService.getOnlineDatasourceRelationListWithRelation(onlineDatasourceRelationFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineDatasourceRelationList, OnlineDatasourceRelation.INSTANCE));
    }

    /**
     * 查看指定数据关联对象详情。
     *
     * @param relationId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<OnlineDatasourceRelationVo> view(@RequestParam Long relationId) {
        if (MyCommonUtil.existBlankArgument(relationId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineDatasourceRelation onlineDatasourceRelation =
                onlineDatasourceRelationService.getByIdWithRelation(relationId, MyRelationParam.full());
        if (onlineDatasourceRelation == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineDatasourceRelationVo onlineDatasourceRelationVo =
                OnlineDatasourceRelation.INSTANCE.fromModel(onlineDatasourceRelation);
        return ResponseResult.success(onlineDatasourceRelationVo);
    }
}
