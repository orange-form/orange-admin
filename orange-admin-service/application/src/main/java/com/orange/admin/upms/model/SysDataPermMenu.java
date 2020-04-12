package com.orange.admin.upms.model;

import lombok.Data;

import javax.persistence.*;

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