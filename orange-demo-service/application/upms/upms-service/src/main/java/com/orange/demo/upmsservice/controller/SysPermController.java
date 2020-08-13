package com.orange.demo.upmsservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.common.core.object.CallResult;
import lombok.extern.slf4j.Slf4j;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.common.core.object.MyPageParam;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.util.MyPageUtil;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.upmsinterface.dto.SysPermDto;
import com.orange.demo.upmsservice.model.SysPerm;
import com.orange.demo.upmsservice.model.SysPermModule;
import com.orange.demo.upmsservice.service.SysPermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Map;

/**
 * 权限资源管理接口控制器类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/sysPerm")
public class SysPermController {

    @Autowired
    private SysPermService sysPermService;

    /**
     * 新增权限资源操作。
     *
     * @param sysPermDto 新增权限资源对象。
     * @return 应答结果对象，包含新增权限资源的主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<JSONObject> add(@MyRequestBody("sysPerm") SysPermDto sysPermDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysPerm sysPerm = MyModelUtil.copyTo(sysPermDto, SysPerm.class);
        CallResult result = sysPermService.verifyRelatedData(sysPerm, null);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        sysPerm = sysPermService.saveNew(sysPerm);
        JSONObject responseData = new JSONObject();
        responseData.put("permId", sysPerm.getPermId());
        return ResponseResult.success(responseData);
    }

    /**
     * 更新权限资源操作。
     *
     * @param sysPermDto 更新权限资源对象。
     * @return 应答结果对象，包含更新权限资源的主键Id。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody("sysPerm") SysPermDto sysPermDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysPermDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysPerm originalPerm = sysPermService.getById(sysPermDto.getPermId());
        if (originalPerm == null) {
            errorMessage = "数据验证失败，当前权限资源并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        SysPerm sysPerm = MyModelUtil.copyTo(sysPermDto, SysPerm.class);
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
    public ResponseResult<SysPermDto> view(@RequestParam Long permId) {
        if (MyCommonUtil.existBlankArgument(permId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysPerm perm = sysPermService.getById(permId);
        if (perm == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysPermDto permDto = MyModelUtil.copyTo(perm, SysPermDto.class);
        return ResponseResult.success(permDto);
    }

    /**
     * 查看权限资源列表。
     *
     * @param sysPermDtoFiltter 过滤对象。
     * @param pageParam         分页参数。
     * @return 应答结果对象，包含权限资源列表。
     */
    @PostMapping("/list")
    public ResponseResult<JSONObject> list(
            @MyRequestBody("sysPermFilter") SysPermDto sysPermDtoFiltter, @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysPerm filter = MyModelUtil.copyTo(sysPermDtoFiltter, SysPerm.class);
        List<SysPerm> permList = sysPermService.getPermListWithRelation(filter);
        List<SysPermDto> permDtoList = MyModelUtil.copyCollectionTo(permList, SysPermDto.class);
        long totalCount = 0L;
        if (permList instanceof Page) {
            totalCount = ((Page<SysPerm>) permList).getTotal();
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(permDtoList, totalCount));
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
    public ResponseResult<JSONObject> listAllPermsByUserFilter(
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
        JSONObject responseData = MyPageUtil.makeResponseData(userPermMapList);
        return ResponseResult.success(responseData);
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
