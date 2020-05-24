package com.orange.admin.upms.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 数据权限与菜单关联实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_data_perm_menu")
public class SysDataPermMenu {

    /**
     * 数据权限Id。
     */
    @Id
    @Column(name = "data_perm_id")
    private Long dataPermId;

    /**
     * 关联菜单Id。
     */
    @Id
    @Column(name = "menu_id")
    private Long menuId;

    @Transient
    private Integer ruleType;

    @Transient
    private String deptIdListString;
}