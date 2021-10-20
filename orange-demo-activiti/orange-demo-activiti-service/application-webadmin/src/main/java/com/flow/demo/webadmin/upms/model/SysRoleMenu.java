package com.flow.demo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 角色菜单实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_role_menu")
public class SysRoleMenu {

    /**
     * 角色Id。
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 菜单Id。
     */
    @TableField(value = "menu_id")
    private Long menuId;
}
