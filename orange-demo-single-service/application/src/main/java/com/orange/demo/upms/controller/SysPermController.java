package com.orange.demo.upms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import com.orange.demo.upms.model.SysPerm;
import com.orange.demo.upms.model.SysPermModule;
import com.orange.demo.upms.service.SysPermService;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.util.MyPageUtil;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Map;

/**
 * 权限资源管理接口控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Api(tags = "权限资源管理接口")
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
    @ApiOperationSupport(ignoreParameters = {"sysPerm.permId"})
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysPerm sysPerm) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPerm);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        CallResult result = sysPermService.verifyRelatedData(sysPerm, null);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        sysPerm = sysPermService.saveNew(sysPerm);
        return ResponseResult.success(sysPerm.getPermId());
    }

    /**
     * 更新权限资源操作。
     *
     * @param sysPerm 更新权限资源对象。
     * @return 应答结果对象，包含更新权限资源的主键Id。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SysPerm sysPerm) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPerm, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysPerm originalPerm = sysPermService.getById(sysPerm.getPermId());
        if (originalPerm == null) {
            errorMessage = "数据验证失败，当前权限资源并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        CallResult result = sysPermService.verifyRelatedData(sysPerm, originalPerm);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        if (result.getData() != null) {
            SysPermModule permModule = (SysPermModule) result.getData().get("permModule");
            sysPerm.setModuleId(permModule.getModuleId());
        }
        sysPermService.update(sysPerm, originalPerm);
        return ResponseResult.success();
    }

    /**
     * 删除指定权限资源操作。
     *
     * @param permId 指定的权限资源主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long permId) {
        if (MyCommonUtil.existBlankArgument(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysPermService.remove(permId)) {
            String errorMessage = "数据操作失败，权限不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 查看权限资源对象详情。
     *
     * @param permId 指定权限资源主键Id。
     * @return 应答结果对象，包含权限资源对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysPerm> view(@RequestParam Long permId) {
        if (MyCommonUtil.existBlankArgument(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysPerm perm = sysPermService.getById(permId);
        if (perm == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(perm);
    }

    /**
     * 查看权限资源列表。
     *
     * @param sysPermFilter 过滤对象。
     * @param pageParam     分页参数。
     * @return 应答结果对象，包含权限资源列表。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysPerm>> list(@MyRequestBody SysPerm sysPermFilter, @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
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
    public ResponseResult<MyPageData<Map<String, Object>>> listAllPermsByUserFilter(
            @MyRequestBody String loginName,
            @MyRequestBody Long moduleId,
            @MyRequestBody String url,
            @MyRequestBody MyPageParam pageParam) {
        if (MyCommonUtil.existBlankArgument(loginName)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<Map<String, Object>> userPermMapList =
                sysPermService.getUserPermListByFilter(loginName, moduleId, url);
        return ResponseResult.success(MyPageUtil.makeResponseData(userPermMapList));
    }

    /**
     * 查看拥有指定权限资源的所有用户数据列表。
     *
     * @param permId 指定权限资源主键Id。
     * @return 应答结果对象，包含用户数据列表。
     */
    @PostMapping("/listAllUsers")
    public ResponseResult<List<Map<String, Object>>> listAllUsers(@MyRequestBody Long permId) {
        if (MyCommonUtil.existBlankArgument(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        List<Map<String, Object>> permUserMapList = sysPermService.getPermUserListById(permId);
        return ResponseResult.success(permUserMapList);
    }

    /**
     * 查看拥有指定权限资源的所有角色数据列表。
     *
     * @param permId 指定权限资源主键Id。
     * @return 应答结果对象，包含角色数据列表。
     */
    @PostMapping("/listAllRoles")
    public ResponseResult<List<Map<String, Object>>> listAllRoles(@MyRequestBody Long permId) {
        if (MyCommonUtil.existBlankArgument(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        List<Map<String, Object>> permRoleMapList = sysPermService.getPermRoleListById(permId);
        return ResponseResult.success(permRoleMapList);
    }
}
