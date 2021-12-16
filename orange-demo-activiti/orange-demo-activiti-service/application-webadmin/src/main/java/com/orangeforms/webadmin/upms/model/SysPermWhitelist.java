package com.orangeforms.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 白名单实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_perm_whitelist")
public class SysPermWhitelist {

    /**
     * 权限资源的URL。
     */
    @TableId(value = "perm_url")
    private String permUrl;

    /**
     * 权限资源所属模块名字(通常是Controller的名字)。
     */
    @TableField(value = "module_name")
    private String moduleName;

    /**
     * 权限的名称。
     */
    @TableField(value = "perm_name")
    private String permName;
}
