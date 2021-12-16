package com.orangeforms.webadmin.upms.vo;

import lombok.Data;

import java.util.*;

/**
 * 菜单VO。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class SysMenuVo {

    /**
     * 菜单Id。
     */
    private Long menuId;

    /**
     * 父菜单Id，目录菜单的父菜单为null
     */
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    private String menuName;

    /**
     * 菜单类型 (0: 目录 1: 菜单 2: 按钮 3: UI片段)。
     */
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
     * 在线表单菜单的权限控制类型，具体值可参考SysOnlineMenuPermType常量对象。
     */
    private Integer onlineMenuPermType;

    /**
     * 仅用于在线表单的流程Id。
     */
    private Long onlineFlowEntryId;

    /**
     * 菜单显示顺序 (值越小，排序越靠前)。
     */
    private Integer showOrder;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 创建者Id。
     */
    private Long createUserId;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新者Id。
     */
    private Long updateUserId;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 菜单与权限字关联对象列表。
     */
    private List<Map<String, Object>> sysMenuPermCodeList;
}
