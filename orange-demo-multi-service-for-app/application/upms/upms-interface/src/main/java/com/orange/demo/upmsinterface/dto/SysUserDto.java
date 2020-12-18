package com.orange.demo.upmsinterface.dto;

import com.orange.demo.common.core.validator.AddGroup;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.upmsinterface.constant.SysUserType;
import com.orange.demo.upmsinterface.constant.SysUserStatus;

import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;

/**
 * SysUserDto对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class SysUserDto {

    /**
     * 用户Id。
     */
    @NotNull(message = "数据验证失败，用户Id不能为空！", groups = {UpdateGroup.class})
    private Long userId;

    /**
     * 登录用户名。
     */
    @NotBlank(message = "数据验证失败，登录用户名不能为空！")
    private String loginName;

    /**
     * 用户密码。
     */
    @NotBlank(message = "数据验证失败，用户密码不能为空！", groups = {AddGroup.class})
    private String password;

    /**
     * 用户显示名称。
     */
    @NotBlank(message = "数据验证失败，用户显示名称不能为空！")
    private String showName;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    @NotNull(message = "数据验证失败，用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)不能为空！")
    @ConstDictRef(constDictClass = SysUserType.class, message = "数据验证失败，用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)为无效值！")
    private Integer userType;

    /**
     * 用户头像的Url。
     */
    private String headImageUrl;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
    @NotNull(message = "数据验证失败，用户状态(0: 正常 1: 锁定)不能为空！")
    @ConstDictRef(constDictClass = SysUserStatus.class, message = "数据验证失败，用户状态(0: 正常 1: 锁定)为无效值！")
    private Integer userStatus;

    /**
     * 创建用户Id。
     */
    private Long createUserId;

    /**
     * 创建用户名。
     */
    private String createUsername;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    private String createTimeEnd;
}
