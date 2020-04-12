package com.orange.admin.upms.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "zz_sys_user_role")
public class SysUserRole {

    /**
     * 用户Id。
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 角色Id。
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;
}
