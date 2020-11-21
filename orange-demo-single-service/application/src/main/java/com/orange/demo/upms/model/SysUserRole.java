package com.orange.demo.upms.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 用户角色实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
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
