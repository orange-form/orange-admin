package com.orange.demo.upms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 菜单与权限字关联实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("菜单与权限字关联实体对象")
@Data
@Table(name = "zz_sys_menu_perm_code")
public class SysMenuPermCode {

    /**
     * 关联菜单Id。
     */
    @ApiModelProperty(value = "关联菜单Id", required = true)
    @Id
    @Column(name = "menu_id")
    private Long menuId;

    /**
     * 关联权限字Id。
     */
    @ApiModelProperty(value = "关联权限字Id", required = true)
    @Id
    @Column(name = "perm_code_id")
    private Long permCodeId;
}