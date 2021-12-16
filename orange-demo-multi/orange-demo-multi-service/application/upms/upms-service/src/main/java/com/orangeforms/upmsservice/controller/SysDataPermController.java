package com.orangeforms.upmsservice.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.log.annotation.OperationLog;
import com.orangeforms.common.log.model.constant.SysOperationLogType;
import com.orangeforms.upmsapi.dto.SysDataPermDto;
import com.orangeforms.upmsapi.dto.SysUserDto;
import com.orangeforms.upmsapi.vo.SysDataPermVo;
import com.orangeforms.upmsapi.vo.SysUserVo;
import com.orangeforms.upmsservice.model.SysDataPerm;
import com.orangeforms.upmsservice.model.SysUser;
import com.orangeforms.upmsservice.service.SysDataPermService;
import com.orangeforms.upmsservice.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限接口控制器对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "数据权限管理接口")
@Slf4j
@RestController
@RequestMapping("/sysDataPerm")
public class SysDataPermController {

    @Autowired
    private SysDataPermService sysDataPermService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 添加新数据权限。
     *
     * @param sysDataPermDto   新增对象。
     * @param deptIdListString 数据权限关联的部门Id列表，多个之间逗号分隔。
     * @return 应答结果对象。包含新增数据权限对象的主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {
            "sysDataPermDto.dataPermId",
            "sysDataPermDto.createTimeStart",
            "sysDataPermDto.createTimeEnd",
            "sysDataPermDto.searchString"})
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody SysDataPermDto sysDataPermDto, @MyRequestBody String deptIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysDataPermDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysDataPerm sysDataPerm = MyModelUtil.copyTo(sysDataPermDto, SysDataPerm.class);
        CallResult result = sysDataPermService.verifyRelatedData(sysDataPerm, deptIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> deptIdSet = null;
        if (result.getData() != null) {
            deptIdSet = result.getData().getObject("deptIdSet", new TypeReference<Set<Long>>(){});
        }
        sysDataPermService.saveNew(sysDataPerm, deptIdSet);
        return ResponseResult.success(sysDataPerm.getDataPermId());
    }

    /**
     * 更新数据权限。
     *
     * @param sysDataPermDto   更新的数据权限对象。
     * @param deptIdListString 数据权限关联的部门Id列表，多个之间逗号分隔。
     * @return 应答结果对象。
     */
    @ApiOperationSupport(ignoreParameters = {
            "sysDataPermDto.createTimeStart",
            "sysDataPermDto.createTimeEnd",
            "sysDataPermDto.searchString"})
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody SysDataPermDto sysDataPermDto, @MyRequestBody String deptIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysDataPermDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysDataPerm originalSysDataPerm = sysDataPermService.getById(sysDataPermDto.getDataPermId());
        if (originalSysDataPerm == null) {
            errorMessage = "数据验证失败，当前数据权限并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        SysDataPerm sysDataPerm = MyModelUtil.copyTo(sysDataPermDto, SysDataPerm.class);
        CallResult result = sysDataPermService.verifyRelatedData(sysDataPerm, deptIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> deptIdSet = null;
        if (result.getData() != null) {
            deptIdSet = result.getData().getObject("deptIdSet", new TypeReference<Set<Long>>(){});
        }
        if (!sysDataPermService.update(sysDataPerm, originalSysDataPerm, deptIdSet)) {
            errorMessage = "更新失败，数据不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 删除数据权限。
     *
     * @param dataPermId 待删除数据权限主键Id。
     * @return 应答数据结果。
     */
    @OperationLog(type = SysOperationLogType.DELETE)
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long dataPermId) {
        if (MyCommonUtil.existBlankArgument(dataPermId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysDataPermService.remove(dataPermId)) {
            String errorMessage = "数据操作失败，数据权限不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 数据权限列表。
     *
     * @param sysDataPermDtoFilter 数据权限查询过滤对象。
     * @param orderParam           排序参数。
     * @param pageParam            分页参数。
     * @return 应答结果对象。包含数据权限列表。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysDataPermVo>> list(
                @MyRequestBody SysDataPermDto sysDataPermDtoFilter,
                @MyRequestBody MyOrderParam orderParam,
                @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysDataPerm filter = MyModelUtil.copyTo(sysDataPermDtoFilter, SysDataPerm.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysDataPerm.class);
        List<SysDataPerm> dataPermList = sysDataPermService.getSysDataPermList(filter, orderBy);
        List<SysDataPermVo> dataPermVoList = MyModelUtil.copyCollectionTo(dataPermList, SysDataPermVo.class);
        long totalCount = 0L;
        if (dataPermList instanceof Page) {
            totalCount = ((Page<SysDataPerm>) dataPermList).getTotal();
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(dataPermVoList, totalCount));
    }

    /**
     * 查看单条数据权限详情。
     *
     * @param dataPermId 数据权限的主键Id。
     * @return 应答结果对象，包含数据权限的详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysDataPermVo> view(@RequestParam Long dataPermId) {
        if (MyCommonUtil.existBlankArgument(dataPermId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysDataPerm sysDataPerm =
                sysDataPermService.getByIdWithRelation(dataPermId, MyRelationParam.full());
        if (sysDataPerm == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysDataPermVo sysDataPermVo = MyModelUtil.copyTo(sysDataPerm, SysDataPermVo.class);
        return ResponseResult.success(sysDataPermVo);
    }

    /**
     * 获取不包含指定数据权限Id的用户列表。
     * 用户和数据权限是多对多关系，当前接口将返回没有赋值指定DataPermId的用户列表。可用于给数据权限添加新用户。
     *
     * @param dataPermId       数据权限主键Id。
     * @param sysUserDtoFilter 用户数据的过滤对象。
     * @param orderParam       排序参数。
     * @param pageParam        分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listNotInDataPermUser")
    public ResponseResult<MyPageData<SysUserVo>> listNotInDataPermUser(
            @MyRequestBody Long dataPermId,
            @MyRequestBody SysUserDto sysUserDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doDataPermUserVerify(dataPermId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysUser filter = MyModelUtil.copyTo(sysUserDtoFilter, SysUser.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
        List<SysUser> userList =
                sysUserService.getNotInSysUserListByDataPermId(dataPermId, filter, orderBy);
        List<SysUserVo> userVoList = MyModelUtil.copyCollectionTo(userList, SysUserVo.class);
        return ResponseResult.success(MyPageUtil.makeResponseData(userVoList));
    }

    /**
     * 拥有指定数据权限的用户列表。
     *
     * @param dataPermId       数据权限Id。
     * @param sysUserDtoFilter 用户过滤对象。
     * @param orderParam       排序参数。
     * @param pageParam        分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listDataPermUser")
    public ResponseResult<MyPageData<SysUserVo>> listDataPermUser(
            @MyRequestBody Long dataPermId,
            @MyRequestBody SysUserDto sysUserDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doDataPermUserVerify(dataPermId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysUser filter = MyModelUtil.copyTo(sysUserDtoFilter, SysUser.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
        List<SysUser> userList =
                sysUserService.getSysUserListByDataPermId(dataPermId, filter, orderBy);
        List<SysUserVo> userVoList = MyModelUtil.copyCollectionTo(userList, SysUserVo.class);
        return ResponseResult.success(MyPageUtil.makeResponseData(userVoList));
    }

    private ResponseResult<Void> doDataPermUserVerify(Long dataPermId) {
        if (MyCommonUtil.existBlankArgument(dataPermId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysDataPermService.existId(dataPermId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        return ResponseResult.success();
    }

    /**
     * 为指定数据权限添加用户列表。该操作可同时给一批用户赋值数据权限，并在同一事务内完成。
     *
     * @param dataPermId       数据权限主键Id。
     * @param userIdListString 逗号分隔的用户Id列表。
     * @return 应答结果对象。
     */
    @OperationLog(type = SysOperationLogType.ADD_M2M)
    @PostMapping("/addDataPermUser")
    public ResponseResult<Void> addDataPermUser(
            @MyRequestBody Long dataPermId, @MyRequestBody String userIdListString) {
        if (MyCommonUtil.existBlankArgument(dataPermId, userIdListString)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        Set<Long> userIdSet =
                Arrays.stream(userIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysDataPermService.existId(dataPermId)
                || !sysUserService.existUniqueKeyList("userId", userIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        sysDataPermService.addDataPermUserList(dataPermId, userIdSet);
        return ResponseResult.success();
    }

    /**
     * 为指定用户移除指定数据权限。
     *
     * @param dataPermId 指定数据权限主键Id。
     * @param userId     指定用户主键Id。
     * @return 应答数据结果。
     */
    @OperationLog(type = SysOperationLogType.DELETE_M2M)
    @PostMapping("/deleteDataPermUser")
    public ResponseResult<Void> deleteDataPermUser(
            @MyRequestBody Long dataPermId, @MyRequestBody Long userId) {
        if (MyCommonUtil.existBlankArgument(dataPermId, userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysDataPermService.removeDataPermUser(dataPermId, userId)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }
}
