package com.orange.demo.upms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 角色菜单实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("角色菜单实体对象")
@Data
@Table(name = "zz_sys_role_menu")
public class SysRoleMenu {

    /**
     * 角色Id。
     */
    @ApiModelProperty(value = "角色Id", required = true)
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 菜单Id。
     */
    @ApiModelProperty(value = "菜单Id", required = true)
    @Id
    @Column(name = "menu_id")
    private Long menuId;
}
