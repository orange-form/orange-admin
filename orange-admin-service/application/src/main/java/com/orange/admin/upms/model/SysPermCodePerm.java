package com.orange.admin.upms.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 权限字与权限资源关联实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
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