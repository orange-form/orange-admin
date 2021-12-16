package com.orangeforms.webadmin.upms.dto;

import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.webadmin.upms.model.constant.SysMenuType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单Dto。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysMenuDto {

    /**
     * 菜单Id。
     */
    @NotNull(message = "菜单Id不能为空！", groups = {UpdateGroup.class})
    private Long menuId;

    /**
     * 父菜单Id，目录菜单的父菜单为null
     */
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    @NotBlank(message = "菜单显示名称不能为空！")
    private String menuName;

    /**
     * 菜单类型 (0: 目录 1: 菜单 2: 按钮 3: UI片段)。
     */
    @NotNull(message = "菜单类型不能为空！")
    @ConstDictRef(constDictClass = SysMenuType.class, message = "数据验证失败，菜单类型为无效值！")
    private Integer menuType;

    /**
     * 前端表单路由名称，仅用于menu_type为1的菜单类型。
     */
    private String formRouterName;

    /**
     * 在线表单主键Id，仅用于在线表单绑定的菜单。
     */
    private Long onlineFormId;

    /**
     * 仅用于在线表单的流程Id。
     */
    private Long onlineFlowEntryId;

    /**
     * 菜单显示顺序 (值越小，排序越靠前)。
     */
    @NotNull(message = "菜单显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 菜单图标。
     */
    private String icon;
}
