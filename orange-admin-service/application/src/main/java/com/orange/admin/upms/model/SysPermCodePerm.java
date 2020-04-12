package com.orange.admin.upms.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "zz_sys_perm_code_perm")
public class SysPermCodePerm {

    /**
     * 权限字Id。
     */
    @Id
    @Column(name = "perm_code_id")
    private Long permCodeId;

    /**
     * 权限Id。
     */
    @Id
    @Column(name = "perm_id")
    private Long permId;
}