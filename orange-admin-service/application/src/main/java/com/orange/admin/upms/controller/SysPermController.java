package com.orange.admin.upms.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import com.orange.admin.upms.model.SysPerm;
import com.orange.admin.upms.model.SysPermModule;
import com.orange.admin.upms.service.SysPermService;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.common.core.object.MyPageParam;
import com.orange.admin.common.core.util.MyCommonUtil;
import com.orange.admin.common.core.util.MyPageUtil;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Map;

/**
 * 权限资源管理接口控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysPerm")
public class SysPermController {

    @Autowired
    private SysPermService sysPermService;

    /**
     * 新增权限资源操作。
     *
     * @param sysPerm 新增权限资源对象。
     * @return 应答结果对象，包含新增权限资源的主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<?> add(@MyRequestBody SysPerm sysPerm) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        JSONObject responseData = null;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysPerm);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            VerifyResult result = sysPermService.verifyRelatedData(sysPerm, null);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            sysPerm = sysPermService.saveNew(sysPerm);
            responseData = new JSONObject();
            responseData.put("permId", sysPerm.getPermId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新权限资源操作。
     *
     * @param sysPerm 更新权限资源对象。
     * @return 应答结果对象，包含更新权限资源的主键Id。
     */
    @PostMapping("/update")
    public ResponseResult<?> update(@MyRequestBody SysPerm sysPerm) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysPerm, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            SysPerm originalPerm = sysPermService.getById(sysPerm.getPermId());
            if (originalPerm == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据验证失败，当前权限资源并不存在，请刷新后重试！";
                break;
            }
            VerifyResult result = sysPermService.verifyRelatedData(sysPerm, originalPerm);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            if (result.getData() != null) {
                SysPermModule permModule = (SysPermModule) result.getData().get("permModule");
                sysPerm.setModuleId(permModule.getModuleId());
            }
            sysPermService.update(sysPerm, originalPerm);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除指定权限资源操作。
     *
     * @param permId 指定的权限资源主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long permId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(permId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysPermService.remove(permId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，权限不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 查看权限资源对象详情。
     *
     * @param permId 指定权限资源主键Id。
     * @return 应答结果对象，包含权限资源对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysPerm> view(@RequestParam Long permId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        SysPerm perm = null;
        do {
            if (MyCommonUtil.existBlankArgument(permId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            perm = sysPermService.getById(permId);
            if (perm == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, perm);
    }

    /**
     * 查看权限资源列表。
     *
     * @param sysPermFilter 过滤对象。
     * @param pageParam     分页参数。
     * @return 应答结果对象，包含权限资源列表。
     */
    @PostMapping("/list")
    public ResponseResult<?> list(@MyRequestBody SysPerm sysPermFilter, @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<SysPerm> resultList = sysPermService.getPermListWithRelation(sysPermFilter);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看用户关联的权限资源列表。
     *
     * @param loginName 精确匹配用户登录名。
     * @param moduleId  精确匹配权限模块Id。
     * @param url       模糊匹配的url过滤条件。
     * @param pageParam 分页对象。
     * @return 应答结果对象，包含该用户的全部权限资源列表。
     */
    @PostMapping("/listAllPermsByUserFilter")
    public ResponseResult<?> listAllPermsByUserFilter(
            @MyRequestBody String loginName,
            @MyRequestBody Long moduleId,
            @MyRequestBody String url,
            @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(loginName)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            List<Map<String, Object>> userPermMapList =
                    sysPermService.getUserPermListByFilter(loginName, moduleId, url);
            responseData = MyPageUtil.makeResponseData(userPermMapList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 查看拥有指定权限资源的所有用户数据列表。
     *
     * @param permId 指定权限资源主键Id。
     * @return 应答结果对象，包含用户数据列表。
     */
    @PostMapping("/listAllUsers")
    public ResponseResult<?> listAllUsers(@MyRequestBody Long permId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        List<Map<String, Object>> permUserMapList = null;
        do {
            if (MyCommonUtil.existBlankArgument(permId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            permUserMapList = sysPermService.getPermUserListById(permId);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, permUserMapList);
    }

    /**
     * 查看拥有指定权限资源的所有角色数据列表。
     *
     * @param permId 指定权限资源主键Id。
     * @return 应答结果对象，包含角色数据列表。
     */
    @PostMapping("/listAllRoles")
    public ResponseResult<?> listAllRoles(@MyRequestBody Long permId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        List<Map<String, Object>> permRoleMapList = null;
        do {
            if (MyCommonUtil.existBlankArgument(permId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            permRoleMapList = sysPermService.getPermRoleListById(permId);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, permRoleMapList);
    }
}
