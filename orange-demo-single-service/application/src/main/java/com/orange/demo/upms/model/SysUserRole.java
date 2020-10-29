package com.orange.demo.upms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 用户角色实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("用户角色实体对象")
@Data
@Table(name = "zz_sys_user_role")
public class SysUserRole {

    /**
     * 用户Id。
     */
    @ApiModelProperty(value = "用户Id", required = true)
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 角色Id。
     */
    @ApiModelProperty(value = "角色Id", required = true)
    @Id
    @Column(name = "role_id")
    private Long roleId;
}
