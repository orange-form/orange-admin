package com.orange.demo.upms.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * SysUserVO对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
public class SysUserVo {

    /**
     * 用户Id。
     */
    private Long userId;

    /**
     * 登录用户名。
     */
    private String loginName;

    /**
     * 用户密码。
     */
    private String password;

    /**
     * 用户显示名称。
     */
    private String showName;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    private Integer userType;

    /**
     * 用户头像的Url。
     */
    private String headImageUrl;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
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
     * userType 常量字典关联数据。
     */
    private Map<String, Object> userTypeDictMap;

    /**
     * userStatus 常量字典关联数据。
     */
    private Map<String, Object> userStatusDictMap;
}
