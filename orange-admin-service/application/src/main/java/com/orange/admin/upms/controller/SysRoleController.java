package com.orange.admin.upms.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import com.orange.admin.upms.model.SysRole;
import com.orange.admin.upms.model.SysUser;
import com.orange.admin.upms.model.SysUserRole;
import com.orange.admin.upms.service.SysRoleService;
import com.orange.admin.upms.service.SysUserService;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.util.MyCommonUtil;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.MyOrderParam;
import com.orange.admin.common.core.object.MyPageParam;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.common.core.util.MyPageUtil;
import com.orange.admin.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色管理接口控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 新增角色操作。
     *
     * @param sysRole          新增角色对象。
     * @param menuIdListString 与当前角色Id绑定的menuId列表，多个menuId之间逗号分隔。
     * @return 应答结果对象，包含新增角色的主键Id。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/add")
    public ResponseResult<?> add(@MyRequestBody SysRole sysRole, @MyRequestBody String menuIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        JSONObject responseData = null;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysRole);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            VerifyResult result = sysRoleService.verifyRelatedData(sysRole, null, menuIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            Set<Long> menuIdSet = null;
            if (result.getData() != null) {
                menuIdSet = (Set<Long>) result.getData().get("menuIdSet");
            }
            sysRoleService.saveNew(sysRole, menuIdSet);
            responseData = new JSONObject();
            responseData.put("roleId", sysRole.getRoleId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新角色操作。
     *
     * @param sysRole          更新角色对象。
     * @param menuIdListString 与当前角色Id绑定的menuId列表，多个menuId之间逗号分隔。
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/update")
    public ResponseResult<?> update(@MyRequestBody SysRole sysRole, @MyRequestBody String menuIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysRole, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            SysRole originalSysRole = sysRoleService.getById(sysRole.getRoleId());
            if (originalSysRole == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据验证失败，当前角色并不存在，请刷新后重试！";
                break;
            }
            VerifyResult result = sysRoleService.verifyRelatedData(sysRole, originalSysRole, menuIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            Set<Long> menuIdSet = null;
            if (result.getData() != null) {
                menuIdSet = (Set<Long>) result.getData().get("menuIdSet");
            }
            if (!sysRoleService.update(sysRole, originalSysRole, menuIdSet)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "更新失败，数据不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除指定角色操作。
     *
     * @param roleId 指定角色主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long roleId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(roleId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysRoleService.remove(roleId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，角色不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 查看角色列表。
     *
     * @param sysRoleFilter 角色过滤对象。
     * @param orderParam    排序参数。
     * @param pageParam     分页参数。
     * @return 应答结果对象，包含角色列表。
     */
    @PostMapping("/list")
    public ResponseResult<?> list(
            @MyRequestBody SysRole sysRoleFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<SysRole> roleList = sysRoleService.getSysRoleList(
                sysRoleFilter, MyOrderParam.buildOrderBy(orderParam, SysRole.class));
        return ResponseResult.success(MyPageUtil.makeResponseData(roleList));
    }

    /**
     * 查看角色详情。
     *
     * @param roleId 指定角色主键Id。
     * @return 应答结果对象，包含角色详情对象。
     */
    @GetMapping("/view")
    public ResponseResult<SysRole> view(@RequestParam Long roleId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        SysRole sysRole = null;
        do {
            if (MyCommonUtil.existBlankArgument(roleId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            sysRole = sysRoleService.getSysRoleWithRelation(roleId);
            if (sysRole == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, sysRole);
    }

    /**
     * 获取不包含指定角色Id的用户列表。
     * 用户和角色是多对多关系，当前接口将返回没有赋值指定RoleId的用户列表。可用于给角色添加新用户。
     *
     * @param roleId        角色主键Id。
     * @param sysUserFilter 用户过滤对象。
     * @param orderParam    排序参数。
     * @param pageParam     分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listNotInUserRole")
    public ResponseResult<?> listNotInUserRole(
            @MyRequestBody Long roleId,
            @MyRequestBody SysUser sysUserFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(roleId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysRoleService.existId(roleId)) {
                errorCodeEnum = ErrorCodeEnum.INVALID_RELATED_RECORD_ID;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
            List<SysUser> resultList =
                    sysUserService.getNotInSysUserListByRoleId(roleId, sysUserFilter, orderBy);
            responseData = MyPageUtil.makeResponseData(resultList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 拥有指定角色的用户列表。
     *
     * @param roleId        角色主键Id。
     * @param sysUserFilter 用户过滤对象。
     * @param orderParam    排序参数。
     * @param pageParam     分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listUserRole")
    public ResponseResult<?> listUserRole(
            @MyRequestBody Long roleId,
            @MyRequestBody SysUser sysUserFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(roleId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysRoleService.existId(roleId)) {
                errorCodeEnum = ErrorCodeEnum.INVALID_RELATED_RECORD_ID;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
            List<SysUser> resultList = sysUserService.getSysUserListByRoleId(roleId, sysUserFilter, orderBy);
            responseData = MyPageUtil.makeResponseData(resultList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 为指定角色添加用户列表。该操作可同时给一批用户赋值角色，并在同一事务内完成。
     *
     * @param roleId           角色主键Id。
     * @param userIdListString 逗号分隔的用户Id列表。
     * @return 应答结果对象。
     */
    @PostMapping("/addUserRole")
    public ResponseResult<?> addUserRole(
            @MyRequestBody Long roleId, @MyRequestBody String userIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(roleId, userIdListString)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            Set<Long> userIdSet = Arrays.stream(
                    userIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            if (!sysRoleService.existId(roleId)
                    || !sysUserService.existUniqueKeyList("userId", userIdSet)) {
                errorCodeEnum = ErrorCodeEnum.INVALID_RELATED_RECORD_ID;
                break;
            }
            List<SysUserRole> userRoleList = new LinkedList<>();
            for (Long userId : userIdSet) {
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(userId);
                userRoleList.add(userRole);
            }
            sysRoleService.addUserRoleList(userRoleList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 为指定用户移除指定角色。
     *
     * @param roleId 指定角色主键Id。
     * @param userId 指定用户主键Id。
     * @return 应答数据结果。
     */
    @PostMapping("/deleteUserRole")
    public ResponseResult<?> deleteUserRole(
            @MyRequestBody Long roleId, @MyRequestBody Long userId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(roleId, userId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysRoleService.removeUserRole(roleId, userId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 通过权限字Id获取拥有改权限的所有角色。
     * 开发人员调试用接口。
     *
     * @param permCodeId 查询的权限字Id。
     * @param pageParam  分页对象。
     * @return 符合条件的角色列表。
     */
    @PostMapping("/listAllRolesByPermCode")
    public ResponseResult<?> listAllRolesByPermCode(
            @MyRequestBody Long permCodeId, @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(permCodeId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            List<SysRole> roleList = sysRoleService.getSysRoleListByPermCodeId(permCodeId);
            responseData = MyPageUtil.makeResponseData(roleList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 通过权限资源url，模糊搜索拥有改权限的所有角色。
     * 开发人员调试用接口。
     *
     * @param url       用于模糊搜索的url。
     * @param pageParam 分页对象。
     * @return 符合条件的角色列表。
     */
    @PostMapping("/listAllRolesByPerm")
    public ResponseResult<?> listAllRolesByPerm(
            @MyRequestBody String url, @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(url)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            List<SysRole> roleList = sysRoleService.getSysRoleListByPerm(url);
            responseData = MyPageUtil.makeResponseData(roleList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }    
}
