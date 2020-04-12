package com.orange.admin.upms.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import com.orange.admin.upms.model.SysDataPermMenu;
import com.orange.admin.upms.model.SysDataPerm;
import com.orange.admin.upms.model.SysUser;
import com.orange.admin.upms.service.SysDataPermService;
import com.orange.admin.upms.service.SysUserService;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.common.core.object.MyOrderParam;
import com.orange.admin.common.core.object.MyPageParam;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.util.MyCommonUtil;
import com.orange.admin.common.core.util.MyPageUtil;
import com.orange.admin.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限接口控制器对象。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysDataPerm")
public class SysDataPermController {

    @Autowired
    private SysDataPermService sysDataPermService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 添加新数据权限操作。
     *
     * @param sysDataPerm      新增对象。
     * @param deptIdListString 数据权限关联的部门Id列表，多个之间逗号分隔。
     * @param menuIdListString 数据权限关联的菜单Id列表，多个之间逗号分隔。
     * @return 应答结果对象。包含新增数据权限对象的主键Id。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/add")
    public ResponseResult<?> add(
            @MyRequestBody SysDataPerm sysDataPerm,
            @MyRequestBody String deptIdListString,
            @MyRequestBody String menuIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(menuIdListString)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            errorMessage = MyCommonUtil.getModelValidationError(sysDataPerm);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            VerifyResult result = sysDataPermService.verifyRelatedData(sysDataPerm, deptIdListString, menuIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            List<SysDataPermMenu> dataPermMenuList = null;
            Set<Long> deptIdSet = null;
            if (result.getData() != null) {
                dataPermMenuList = (List<SysDataPermMenu>) result.getData().get("dataPermMenuList");
                deptIdSet = (Set<Long>) result.getData().get("deptIdSet");
            }
            sysDataPermService.saveNew(sysDataPerm, deptIdSet, dataPermMenuList);
            responseData = new JSONObject();
            responseData.put("dataPermId", sysDataPerm.getDataPermId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新数据权限操作。
     *
     * @param sysDataPerm      更新的数据权限对象。
     * @param deptIdListString 数据权限关联的部门Id列表，多个之间逗号分隔。
     * @param menuIdListString 数据权限关联的菜单Id列表，多个之间逗号分隔
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/update")
    public ResponseResult<?> update(
            @MyRequestBody SysDataPerm sysDataPerm,
            @MyRequestBody String deptIdListString,
            @MyRequestBody String menuIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(menuIdListString)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            errorMessage = MyCommonUtil.getModelValidationError(sysDataPerm, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            SysDataPerm originalSysDataPerm = sysDataPermService.getById(sysDataPerm.getDataPermId());
            if (originalSysDataPerm == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据验证失败，当前数据权限并不存在，请刷新后重试！";
                break;
            }
            VerifyResult result = sysDataPermService.verifyRelatedData(sysDataPerm, deptIdListString, menuIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            List<SysDataPermMenu> dataPermMenuList = null;
            Set<Long> deptIdSet = null;
            if (result.getData() != null) {
                dataPermMenuList = (List<SysDataPermMenu>) result.getData().get("dataPermMenuList");
                deptIdSet = (Set<Long>) result.getData().get("deptIdSet");
            }
            if (!sysDataPermService.update(sysDataPerm, originalSysDataPerm, deptIdSet, dataPermMenuList)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "更新失败，数据不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除数据权限操作。
     *
     * @param dataPermId 待删除数据权限主键Id。
     * @return 应答数据结果。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long dataPermId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(dataPermId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysDataPermService.remove(dataPermId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，数据权限不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 查看数据权限列表。
     *
     * @param sysDataPermFilter 数据权限查询过滤对象。
     * @param orderParam        排序参数。
     * @param pageParam         分页参数。
     * @return 应答结果对象。包含数据权限列表。
     */
    @PostMapping("/list")
    public ResponseResult<?> list(
            @MyRequestBody SysDataPerm sysDataPermFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysDataPerm.class);
        List<SysDataPerm> resultList = sysDataPermService.getSysDataPermList(sysDataPermFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看单条数据权限详情。
     *
     * @param dataPermId 数据权限的主键Id。
     * @return 应答结果对象，包含数据权限的详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysDataPerm> view(@RequestParam Long dataPermId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        SysDataPerm dataPerm = null;
        do {
            if (MyCommonUtil.existBlankArgument(dataPermId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            dataPerm = sysDataPermService.getSysDataPermWithRelation(dataPermId);
            if (dataPerm == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, dataPerm);
    }

    /**
     * 获取不包含指定数据权限Id的用户列表。
     * 用户和数据权限是多对多关系，当前接口将返回没有赋值指定DataPermId的用户列表。可用于给数据权限添加新用户。
     *
     * @param dataPermId    数据权限主键Id。
     * @param sysUserFilter 用户数据的过滤对象。
     * @param orderParam    排序参数。
     * @param pageParam     分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listNotInDataPermUser")
    public ResponseResult<?> listNotInDataPermUser(
            @MyRequestBody Long dataPermId,
            @MyRequestBody SysUser sysUserFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(dataPermId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysDataPermService.existId(dataPermId)) {
                errorCodeEnum = ErrorCodeEnum.INVALID_RELATED_RECORD_ID;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
            List<SysUser> resultList =
                    sysUserService.getNotInSysUserListByDataPermId(dataPermId, sysUserFilter, orderBy);
            responseData = MyPageUtil.makeResponseData(resultList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 拥有指定数据权限的用户列表。
     *
     * @param dataPermId    数据权限主键Id。
     * @param sysUserFilter 用户过滤对象。
     * @param orderParam    排序参数。
     * @param pageParam     分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listDataPermUser")
    public ResponseResult<?> listDataPermUser(
            @MyRequestBody Long dataPermId,
            @MyRequestBody SysUser sysUserFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(dataPermId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysDataPermService.existId(dataPermId)) {
                errorCodeEnum = ErrorCodeEnum.INVALID_RELATED_RECORD_ID;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
            List<SysUser> resultList =
                    sysUserService.getSysUserListByDataPermId(dataPermId, sysUserFilter, orderBy);
            responseData = MyPageUtil.makeResponseData(resultList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 为指定数据权限添加用户列表。该操作可同时给一批用户赋值数据权限，并在同一事务内完成。
     *
     * @param dataPermId       数据权限主键Id。
     * @param userIdListString 逗号分隔的用户Id列表。
     * @return 应答结果对象。
     */
    @PostMapping("/addDataPermUser")
    public ResponseResult<?> addDataPermUser(
            @MyRequestBody Long dataPermId, @MyRequestBody String userIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(dataPermId, userIdListString)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            Set<Long> userIdSet =
                    Arrays.stream(userIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            if (!sysDataPermService.existId(dataPermId)
                    || !sysUserService.existUniqueKeyList("userId", userIdSet)) {
                errorCodeEnum = ErrorCodeEnum.INVALID_RELATED_RECORD_ID;
                break;
            }
            sysDataPermService.addDataPermUserList(dataPermId, userIdSet);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 为指定用户移除指定数据权限。
     *
     * @param dataPermId 指定数据权限主键Id。
     * @param userId     指定用户主键Id。
     * @return 应答数据结果。
     */
    @PostMapping("/deleteDataPermUser")
    public ResponseResult<?> deleteDataPermUser(
            @MyRequestBody Long dataPermId, @MyRequestBody Long userId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(dataPermId, userId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysDataPermService.removeDataPermUser(dataPermId, userId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }
}
