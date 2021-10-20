package com.flow.demo.webadmin.upms.dto;

import com.flow.demo.common.core.validator.AddGroup;
import com.flow.demo.common.core.validator.UpdateGroup;
import com.flow.demo.common.core.validator.ConstDictRef;
import com.flow.demo.webadmin.upms.model.constant.SysUserType;
import com.flow.demo.webadmin.upms.model.constant.SysUserStatus;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * SysUserDto对象。
 *
 * @author Jerry
 * @date 2021-06-06
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
     * 用户部门Id。
     */
    @NotNull(message = "数据验证失败，用户部门Id不能为空！")
    private Long deptId;

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
     * createTime 范围过滤起始值(>=)。
     */
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    private String createTimeEnd;
}
