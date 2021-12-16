package com.orangeforms.common.online.api.controller;

import com.github.pagehelper.page.PageMethod;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.*;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.dto.OnlineVirtualColumnDto;
import com.orangeforms.common.online.model.OnlineVirtualColumn;
import com.orangeforms.common.online.model.constant.VirtualType;
import com.orangeforms.common.online.service.OnlineVirtualColumnService;
import com.orangeforms.common.online.vo.OnlineVirtualColumnVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;

/**
 * 虚拟字段操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineVirtualColumn")
public class OnlineVirtualColumnController {

    @Autowired
    private OnlineVirtualColumnService onlineVirtualColumnService;

    /**
     * 新增虚拟字段数据。
     *
     * @param onlineVirtualColumnDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody OnlineVirtualColumnDto onlineVirtualColumnDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineVirtualColumnDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineVirtualColumn onlineVirtualColumn =
                MyModelUtil.copyTo(onlineVirtualColumnDto, OnlineVirtualColumn.class);
        ResponseResult<Void> verifyResult = this.doVerify(onlineVirtualColumn, null);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        onlineVirtualColumn = onlineVirtualColumnService.saveNew(onlineVirtualColumn);
        return ResponseResult.success(onlineVirtualColumn.getVirtualColumnId());
    }

    /**
     * 更新虚拟字段数据。
     *
     * @param onlineVirtualColumnDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineVirtualColumnDto onlineVirtualColumnDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(
                onlineVirtualColumnDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineVirtualColumn onlineVirtualColumn =
                MyModelUtil.copyTo(onlineVirtualColumnDto, OnlineVirtualColumn.class);
        OnlineVirtualColumn originalOnlineVirtualColumn =
                onlineVirtualColumnService.getById(onlineVirtualColumn.getVirtualColumnId());
        if (originalOnlineVirtualColumn == null) {
            errorMessage = "数据验证失败，当前虚拟字段并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        ResponseResult<Void> verifyResult = this.doVerify(onlineVirtualColumn, originalOnlineVirtualColumn);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (!onlineVirtualColumnService.update(onlineVirtualColumn, originalOnlineVirtualColumn)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除虚拟字段数据。
     *
     * @param virtualColumnId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long virtualColumnId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(virtualColumnId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        OnlineVirtualColumn originalOnlineVirtualColumn = onlineVirtualColumnService.getById(virtualColumnId);
        if (originalOnlineVirtualColumn == null) {
            errorMessage = "数据验证失败，当前虚拟字段并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineVirtualColumnService.remove(virtualColumnId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的虚拟字段列表。
     *
     * @param onlineVirtualColumnDtoFilter 过滤对象。
     * @param orderParam                   排序参数。
     * @param pageParam                    分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineVirtualColumnVo>> list(
            @MyRequestBody OnlineVirtualColumnDto onlineVirtualColumnDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineVirtualColumn onlineVirtualColumnFilter =
                MyModelUtil.copyTo(onlineVirtualColumnDtoFilter, OnlineVirtualColumn.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineVirtualColumn.class);
        List<OnlineVirtualColumn> onlineVirtualColumnList =
                onlineVirtualColumnService.getOnlineVirtualColumnListWithRelation(onlineVirtualColumnFilter, orderBy);
        MyPageData<OnlineVirtualColumnVo> pageData =
                MyPageUtil.makeResponseData(onlineVirtualColumnList, OnlineVirtualColumn.INSTANCE);
        return ResponseResult.success(pageData);
    }

    /**
     * 查看指定虚拟字段对象详情。
     *
     * @param virtualColumnId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<OnlineVirtualColumnVo> view(@RequestParam Long virtualColumnId) {
        if (MyCommonUtil.existBlankArgument(virtualColumnId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineVirtualColumn onlineVirtualColumn =
                onlineVirtualColumnService.getByIdWithRelation(virtualColumnId, MyRelationParam.full());
        if (onlineVirtualColumn == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineVirtualColumnVo onlineVirtualColumnVo =
                OnlineVirtualColumn.INSTANCE.fromModel(onlineVirtualColumn);
        return ResponseResult.success(onlineVirtualColumnVo);
    }

    private ResponseResult<Void> doVerify(
            OnlineVirtualColumn virtualColumn, OnlineVirtualColumn originalVirtualColumn) {
        if (!virtualColumn.getVirtualType().equals(VirtualType.AGGREGATION)) {
            return ResponseResult.success();
        }
        if (MyCommonUtil.existBlankArgument(
                virtualColumn.getAggregationColumnId(),
                virtualColumn.getAggregationTableId(),
                virtualColumn.getDatasourceId(),
                virtualColumn.getRelationId(),
                virtualColumn.getAggregationType())) {
            String errorMessage = "数据验证失败，数据源、关联关系、聚合表、聚合字段和聚合类型，均不能为空！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        CallResult verifyResult = onlineVirtualColumnService.verifyRelatedData(virtualColumn, null);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        return ResponseResult.success();
    }
}
