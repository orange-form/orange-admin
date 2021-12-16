package com.orangeforms.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 权限字与权限资源关联实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_perm_code_perm")
public class SysPermCodePerm {

    /**
     * 权限字Id。
     */
    @TableField(value = "perm_code_id")
    private Long permCodeId;

    /**
     * 权限Id。
     */
    @TableField(value = "perm_id")
    private Long permId;
}
