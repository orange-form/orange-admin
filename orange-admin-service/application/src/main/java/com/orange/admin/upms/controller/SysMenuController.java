package com.orange.admin.upms.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.orange.admin.upms.model.SysMenu;
import com.orange.admin.upms.service.SysMenuService;
import com.orange.admin.upms.service.SysPermCodeService;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.util.MyCommonUtil;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 菜单管理接口控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysMenu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysPermCodeService sysPermCodeService;

    /**
     * 添加新菜单操作。
     *
     * @param sysMenu              新菜单对象。
     * @param permCodeIdListString 与当前菜单Id绑定的权限Id列表，多个权限之间逗号分隔。
     * @return 应答结果对象，包含新增菜单的主键Id。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/add")
    public ResponseResult<?> add(@MyRequestBody SysMenu sysMenu, @MyRequestBody String permCodeIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        JSONObject responseData = null;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysMenu);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            VerifyResult result = sysMenuService.verifyRelatedData(sysMenu, null, permCodeIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            Set<Long> permCodeIdSet = null;
            if (result.getData() != null) {
                permCodeIdSet = (Set<Long>) result.getData().get("permCodeIdSet");
            }
            sysMenuService.saveNew(sysMenu, permCodeIdSet);
            responseData = new JSONObject();
            responseData.put("sysMenuId", sysMenu.getMenuId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新菜单数据操作。
     *
     * @param sysMenu              更新菜单对象。
     * @param permCodeIdListString 与当前菜单Id绑定的权限Id列表，多个权限之间逗号分隔。
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/update")
    public ResponseResult<?> update(@MyRequestBody SysMenu sysMenu, @MyRequestBody String permCodeIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysMenu, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            SysMenu originalSysMenu = sysMenuService.getById(sysMenu.getMenuId());
            if (originalSysMenu == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据验证失败，当前菜单并不存在，请刷新后重试！";
                break;
            }
            VerifyResult result = sysMenuService.verifyRelatedData(sysMenu, originalSysMenu, permCodeIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            Set<Long> permCodeIdSet = null;
            if (result.getData() != null) {
                permCodeIdSet = (Set<Long>) result.getData().get("permCodeIdSet");
            }
            if (!sysMenuService.update(sysMenu, originalSysMenu, permCodeIdSet)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据验证失败，当前权限字并不存在，请刷新后重试！";
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除指定菜单操作。
     *
     * @param menuId 指定菜单主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long menuId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(menuId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (sysMenuService.hasChildren(menuId)) {
                errorCodeEnum = ErrorCodeEnum.HAS_CHILDREN_DATA;
                errorMessage = "数据验证失败，当前菜单存在下级菜单！";
                break;
            }
            if (!sysMenuService.remove(menuId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，菜单不存在，请刷新后重试！";
            }

        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 获取全部菜单列表。
     *
     * @return 应答结果对象，包含全部菜单数据列表。
     */
    @GetMapping("/list")
    public ResponseResult<List<SysMenu>> list() {
        return ResponseResult.success(sysMenuService.getAllListByOrder("showOrder"));
    }

    /**
     * 查看指定菜单数据详情。
     *
     * @param menuId 指定菜单主键Id。
     * @return 应答结果对象，包含菜单详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysMenu> view(@RequestParam Long menuId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        SysMenu sysMenu = null;
        do {
            if (MyCommonUtil.existBlankArgument(menuId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            sysMenu = sysMenuService.getSysMenuWithRelation(menuId);
            if (sysMenu == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, sysMenu);
    }

    /**
     * 列出与指定菜单关联的权限字和权限资源，便于管理员排查配置中的错误。
     *
     * @param menuId 菜单Id。
     * @return 与菜单关联的权限字和权限资源列表。
     */
    @GetMapping("/listMenuPerm")
    public ResponseResult<List<Map<String, Object>>> listMenuPerm(@RequestParam Long menuId) {
        return ResponseResult.success(sysPermCodeService.getPermCodeListByMenuId(menuId));
    }
}
