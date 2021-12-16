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
import com.orangeforms.common.online.dto.OnlineDatasourceDto;
import com.orangeforms.common.online.model.*;
import com.orangeforms.common.online.model.constant.PageType;
import com.orangeforms.common.online.object.SqlTable;
import com.orangeforms.common.online.object.SqlTableColumn;
import com.orangeforms.common.online.service.*;
import com.orangeforms.common.online.vo.OnlineDatasourceVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 数据模型操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineDatasource")
public class OnlineDatasourceController {

    @Autowired
    private OnlineDatasourceService onlineDatasourceService;
    @Autowired
    private OnlineFormService onlineFormService;
    @Autowired
    private OnlinePageService onlinePageService;
    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlineDblinkService onlineDblinkService;

    /**
     * 新增数据模型数据。
     *
     * @param onlineDatasourceDto 新增对象。
     * @param pageId              关联的页面Id。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody OnlineDatasourceDto onlineDatasourceDto,
            @MyRequestBody(required = true) Long pageId) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineDatasourceDto, Default.class, AddGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlinePage onlinePage = onlinePageService.getById(pageId);
        if (onlinePage == null) {
            errorMessage = "数据验证失败，页面Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineDatasource onlineDatasource = MyModelUtil.copyTo(onlineDatasourceDto, OnlineDatasource.class);
        OnlineDblink onlineDblink = onlineDblinkService.getById(onlineDatasourceDto.getDblinkId());
        if (onlineDblink == null) {
            errorMessage = "数据验证失败，关联的数据库链接Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SqlTable sqlTable = onlineDblinkService.getDblinkTable(onlineDblink, onlineDatasourceDto.getMasterTableName());
        if (sqlTable == null) {
            errorMessage = "数据验证失败，指定的数据表名不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 流程表单的主表主键，不能是自增主键。
        if (onlinePage.getPageType().equals(PageType.FLOW)) {
            for (SqlTableColumn tableColumn : sqlTable.getColumnList()) {
                if (tableColumn.getPrimaryKey()) {
                    if (tableColumn.getAutoIncrement()) {
                        errorMessage = "数据验证失败，流程页面所关联的主表主键，不能是自增主键！";
                        return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
                    }
                    break;
                }
            }
        }
        onlineDatasource = onlineDatasourceService.saveNew(onlineDatasource, sqlTable, pageId);
        return ResponseResult.success(onlineDatasource.getDatasourceId());
    }

    /**
     * 更新数据模型数据。
     *
     * @param onlineDatasourceDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineDatasourceDto onlineDatasourceDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineDatasourceDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineDatasource onlineDatasource = MyModelUtil.copyTo(onlineDatasourceDto, OnlineDatasource.class);
        OnlineDatasource originalOnlineDatasource = onlineDatasourceService.getById(onlineDatasource.getDatasourceId());
        if (originalOnlineDatasource == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前在线数据源并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasource.getDblinkId().equals(originalOnlineDatasource.getDatasourceId())) {
            errorMessage = "数据验证失败，不能修改数据库链接Id！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasource.getMasterTableId().equals(originalOnlineDatasource.getDatasourceId())) {
            errorMessage = "数据验证失败，不能修改主表Id！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasourceService.update(onlineDatasource, originalOnlineDatasource)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除数据模型数据。
     *
     * @param datasourceId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long datasourceId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(datasourceId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        OnlineDatasource originalOnlineDatasource = onlineDatasourceService.getById(datasourceId);
        if (originalOnlineDatasource == null) {
            errorMessage = "数据验证失败，当前数据源并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        List<OnlineForm> formList = onlineFormService.getOnlineFormListByDatasourceId(datasourceId);
        if (CollUtil.isNotEmpty(formList)) {
            errorMessage = "数据验证失败，当前数据源正在被 [" + formList.get(0).getFormName() + "] 表单占用，请先删除关联数据！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDatasourceService.remove(datasourceId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的数据模型列表。
     *
     * @param onlineDatasourceDtoFilter 过滤对象。
     * @param orderParam                排序参数。
     * @param pageParam                 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineDatasourceVo>> list(
            @MyRequestBody OnlineDatasourceDto onlineDatasourceDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineDatasource onlineDatasourceFilter = MyModelUtil.copyTo(onlineDatasourceDtoFilter, OnlineDatasource.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineDatasource.class);
        List<OnlineDatasource> onlineDatasourceList =
                onlineDatasourceService.getOnlineDatasourceListWithRelation(onlineDatasourceFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineDatasourceList, OnlineDatasource.INSTANCE));
    }

    /**
     * 查看指定数据模型对象详情。
     *
     * @param datasourceId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<OnlineDatasourceVo> view(@RequestParam Long datasourceId) {
        if (MyCommonUtil.existBlankArgument(datasourceId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineDatasource onlineDatasource =
                onlineDatasourceService.getByIdWithRelation(datasourceId, MyRelationParam.full());
        if (onlineDatasource == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineDatasourceVo onlineDatasourceVo = OnlineDatasource.INSTANCE.fromModel(onlineDatasource);
        List<OnlineTable> tableList = onlineTableService.getOnlineTableListByDatasourceId(datasourceId);
        if (CollUtil.isNotEmpty(tableList)) {
            onlineDatasourceVo.setTableList(OnlineTable.INSTANCE.fromModelList(tableList));
        }
        return ResponseResult.success(onlineDatasourceVo);
    }
}
