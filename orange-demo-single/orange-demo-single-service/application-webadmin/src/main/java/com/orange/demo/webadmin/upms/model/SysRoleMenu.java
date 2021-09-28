package com.orange.demo.webadmin.upms.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 角色菜单实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_sys_role_menu")
public class SysRoleMenu {

    /**
     * 角色Id。
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 菜单Id。
     */
    @Id
    @Column(name = "menu_id")
    private Long menuId;
}
