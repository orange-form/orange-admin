package com.orangeforms.uaaauth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.base.model.BaseModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * SysUaaUser实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "zz_sys_uaa_user")
public class SysUaaUser extends BaseModel {

    /**
     * 用户Id。
     */
    @NotNull(message = "数据验证失败，用户Id不能为空！")
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 登录用户名。
     */
    @NotBlank(message = "数据验证失败，用户名不能为空！")
    private String username;

    /**
     * 用户密码。
     */
    @NotBlank(message = "数据验证失败，用户密码不能为空！")
    private String password;

    /**
     * 用户显示名称。
     */
    @NotBlank(message = "数据验证失败，用户显示名称不能为空！")
    @TableField(value = "show_name")
    private String showName;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
    @NotNull(message = "数据验证失败，用户状态不能为空！")
    private Boolean locked;

    /**
     * 是否删除(1: 正常，-1： 删除)
     */
    @JSONField(serialize = false)
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;
}
