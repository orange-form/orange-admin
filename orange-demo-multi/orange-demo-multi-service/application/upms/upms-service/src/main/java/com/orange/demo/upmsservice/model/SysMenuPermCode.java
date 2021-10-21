package com.orange.demo.upmsservice.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 菜单与权限字关联实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@TableName(value = "zz_sys_menu_perm_code")
public class SysMenuPermCode {

    /**
     * 关联菜单Id。
     */
    @TableField(value = "menu_id")
    private Long menuId;

    /**
     * 关联权限字Id。
     */
    @TableField(value = "perm_code_id")
    private Long permCodeId;
}
