package com.orange.admin.upms.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.orange.admin.upms.model.SysPerm;
import com.orange.admin.upms.model.SysPermModule;
import com.orange.admin.upms.service.SysPermModuleService;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.util.MyCommonUtil;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.annotation.MyRequestBody;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 权限资源模块管理接口控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysPermModule")
public class SysPermModuleController {

    @Autowired
    private SysPermModuleService sysPermModuleService;

    /**
     * 新增权限资源模块操作。
     *
     * @param sysPermModule 新增权限资源模块对象。
     * @return 应答结果对象，包含新增权限资源模块的主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<?> add(@MyRequestBody SysPermModule sysPermModule) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        JSONObject responseData = null;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysPermModule);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            if (sysPermModule.getParentId() != null) {
                if (sysPermModuleService.getById(sysPermModule.getParentId()) == null) {
                    errorCodeEnum = ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST;
                    errorMessage = "数据验证失败，关联的上级权限模块并不存在，请刷新后重试！";
                    break;
                }
            }
            sysPermModuleService.saveNew(sysPermModule);
            responseData = new JSONObject();
            responseData.put("permModuleId", sysPermModule.getModuleId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新权限资源模块操作。
     *
     * @param sysPermModule 更新权限资源模块对象。
     * @return 应答结果对象，包含新增权限资源模块的主键Id。
     */
    @PostMapping("/update")
    public ResponseResult<?> update(@MyRequestBody SysPermModule sysPermModule) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysPermModule, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            SysPermModule originalPermModule = sysPermModuleService.getById(sysPermModule.getModuleId());
            if (originalPermModule == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
            if (sysPermModule.getParentId() != null
                    && !sysPermModule.getParentId().equals(originalPermModule.getParentId())) {
                if (sysPermModuleService.getById(sysPermModule.getParentId()) == null) {
                    errorCodeEnum = ErrorCodeEnum.DATA_PARENT_ID_NOT_EXIST;
                    errorMessage = "数据验证失败，关联的上级权限模块并不存在，请刷新后重试！";
                    break;
                }
            }
            if (!sysPermModuleService.update(sysPermModule, originalPermModule)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据验证失败，当前模块并不存在，请刷新后重试！";
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除指定权限资源模块操作。
     *
     * @param moduleId 指定的权限资源模块主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long moduleId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(moduleId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (sysPermModuleService.hasChildren(moduleId)
                    || sysPermModuleService.hasModulePerms(moduleId)) {
                errorCodeEnum = ErrorCodeEnum.HAS_CHILDREN_DATA;
                errorMessage = "数据验证失败，当前权限模块存在子模块或权限资源，请先删除关联数据！";
                break;
            }
            if (!sysPermModuleService.remove(moduleId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，权限模块不存在，请刷新后重试！";
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 查看全部权限资源模块列表。
     *
     * @return 应答结果对象，包含权限资源模块列表。
     */
    @GetMapping("/list")
    public ResponseResult<List<SysPermModule>> list() {
        return ResponseResult.success(sysPermModuleService.getAllListByOrder("showOrder"));
    }

    /**
     * 列出全部权限资源模块及其下级关联的权限资源列表。
     *
     * @return 应答结果对象，包含树状列表，结构为权限资源模块和权限资源之间的树状关系。
     */
    @GetMapping("/listAll")
    public ResponseResult<?> listAll() {
        List<SysPermModule> sysPermModuleList = sysPermModuleService.getPermModuleAndPermList();
        List<Map<String, Object>> resultList = new LinkedList<>();
        for (SysPermModule sysPermModule : sysPermModuleList) {
            Map<String, Object> permModuleMap = new HashMap<>(5);
            permModuleMap.put("id", sysPermModule.getModuleId());
            permModuleMap.put("name", sysPermModule.getModuleName());
            permModuleMap.put("type", sysPermModule.getModuleType());
            permModuleMap.put("isPerm", false);
            if (MyCommonUtil.isNotBlankOrNull(sysPermModule.getParentId())) {
                permModuleMap.put("parentId", sysPermModule.getParentId());
            }
            resultList.add(permModuleMap);
            if (CollectionUtils.isNotEmpty(sysPermModule.getSysPermList())) {
                for (SysPerm sysPerm : sysPermModule.getSysPermList()) {
                    Map<String, Object> permMap = new HashMap<>(4);
                    permMap.put("id", sysPerm.getPermId());
                    permMap.put("name", sysPerm.getPermName());
                    permMap.put("isPerm", true);
                    permMap.put("url", sysPerm.getUrl());
                    permMap.put("parentId", sysPermModule.getModuleId());
                    resultList.add(permMap);
                }
            }
        }
        return ResponseResult.success(resultList);
    }
}
