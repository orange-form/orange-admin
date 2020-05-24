package com.orange.admin.upms.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 菜单与权限字关联实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_menu_perm_code")
public class SysMenuPermCode {

    /**
     * 关联菜单Id。
     */
    @Id
    @Column(name = "menu_id")
    private Long menuId;

    /**
     * 关联权限字Id。
     */
    @Id
    @Column(name = "perm_code_id")
    private Long permCodeId;
}