package com.orange.demo.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.demo.upms.model.constant.SysUserType;
import com.orange.demo.upms.model.constant.SysUserStatus;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.validator.AddGroup;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * SysUser实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_sys_user")
public class SysUser {

    /**
     * 用户Id。
     */
    @NotNull(message = "数据验证失败，用户Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 登录用户名。
     */
    @NotBlank(message = "数据验证失败，登录用户名不能为空！")
    @Column(name = "login_name")
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
    @Column(name = "show_name")
    private String showName;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    @NotNull(message = "数据验证失败，用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)不能为空！")
    @ConstDictRef(constDictClass = SysUserType.class, message = "数据验证失败，用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)为无效值！")
    @Column(name = "user_type")
    private Integer userType;

    /**
     * 用户头像的Url。
     */
    @Column(name = "head_image_url")
    private String headImageUrl;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
    @NotNull(message = "数据验证失败，用户状态(0: 正常 1: 锁定)不能为空！")
    @ConstDictRef(constDictClass = SysUserStatus.class, message = "数据验证失败，用户状态(0: 正常 1: 锁定)为无效值！")
    @Column(name = "user_status")
    private Integer userStatus;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    /**
     * 创建用户Id。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建用户名。
     */
    @Column(name = "create_username")
    private String createUsername;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间。
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    @Transient
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @Transient
    private String createTimeEnd;

    @RelationConstDict(
            masterIdField = "userType",
            constantDictClass = SysUserType.class)
    @Transient
    private Map<String, Object> userTypeDictMap;

    @RelationConstDict(
            masterIdField = "userStatus",
            constantDictClass = SysUserStatus.class)
    @Transient
    private Map<String, Object> userStatusDictMap;
}
