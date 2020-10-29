package com.orange.demo.upms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 权限字与权限资源关联实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("权限字与权限资源关联实体对象")
@Data
@Table(name = "zz_sys_perm_code_perm")
public class SysPermCodePerm {

    /**
     * 权限字Id。
     */
    @ApiModelProperty(value = "权限字Id", required = true)
    @Id
    @Column(name = "perm_code_id")
    private Long permCodeId;

    /**
     * 权限Id。
     */
    @ApiModelProperty(value = "权限Id", required = true)
    @Id
    @Column(name = "perm_id")
    private Long permId;
}