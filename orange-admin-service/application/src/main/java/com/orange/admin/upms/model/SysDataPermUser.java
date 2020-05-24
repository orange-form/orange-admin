package com.orange.admin.upms.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 数据权限与用户关联实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_data_perm_user")
public class SysDataPermUser {

    /**
     * 数据权限Id。
     */
    @Id
    @Column(name = "data_perm_id")
    private Long dataPermId;

    /**
     * 用户Id。
     */
    @Id
    @Column(name = "user_id")
    private Long userId;
}