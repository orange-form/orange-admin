package com.orange.demo.upms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import com.orange.demo.upms.model.SysMenu;
import com.orange.demo.upms.service.SysMenuService;
import com.orange.demo.upms.service.SysPermCodeService;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 菜单管理接口控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Api(tags = "菜单管理接口")
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
    @ApiOperationSupport(ignoreParameters = {"sysMenu.menuId"})
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysMenu sysMenu, @MyRequestBody String permCodeIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysMenu);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        CallResult result = sysMenuService.verifyRelatedData(sysMenu, null, permCodeIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        Set<Long> permCodeIdSet = null;
        if (result.getData() != null) {
            permCodeIdSet = (Set<Long>) result.getData().get("permCodeIdSet");
        }
        sysMenuService.saveNew(sysMenu, permCodeIdSet);
        return ResponseResult.success(sysMenu.getMenuId());
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
    public ResponseResult<Void> update(@MyRequestBody SysMenu sysMenu, @MyRequestBody String permCodeIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysMenu, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysMenu originalSysMenu = sysMenuService.getById(sysMenu.getMenuId());
        if (originalSysMenu == null) {
            errorMessage = "数据验证失败，当前菜单并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        CallResult result = sysMenuService.verifyRelatedData(sysMenu, originalSysMenu, permCodeIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        Set<Long> permCodeIdSet = null;
        if (result.getData() != null) {
            permCodeIdSet = (Set<Long>) result.getData().get("permCodeIdSet");
        }
        if (!sysMenuService.update(sysMenu, originalSysMenu, permCodeIdSet)) {
            errorMessage = "数据验证失败，当前权限字并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 删除指定菜单操作。
     *
     * @param menuId 指定菜单主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long menuId) {
        if (MyCommonUtil.existBlankArgument(menuId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        String errorMessage;
        if (sysMenuService.hasChildren(menuId)) {
            errorMessage = "数据验证失败，当前菜单存在下级菜单！";
            return ResponseResult.error(ErrorCodeEnum.HAS_CHILDREN_DATA, errorMessage);
        }
        if (!sysMenuService.remove(menuId)) {
            errorMessage = "数据操作失败，菜单不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
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
        if (MyCommonUtil.existBlankArgument(menuId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysMenu sysMenu = sysMenuService.getByIdWithRelation(menuId, MyRelationParam.full());
        if (sysMenu == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(sysMenu);
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
