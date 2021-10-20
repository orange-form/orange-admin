package com.flow.demo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 数据权限与用户关联实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_data_perm_user")
public class SysDataPermUser {

    /**
     * 数据权限Id。
     */
    @TableField(value = "data_perm_id")
    private Long dataPermId;

    /**
     * 用户Id。
     */
    @TableField(value = "user_id")
    private Long userId;
}
