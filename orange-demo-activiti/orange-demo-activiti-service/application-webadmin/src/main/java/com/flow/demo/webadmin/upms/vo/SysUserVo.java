package com.flow.demo.webadmin.upms.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * SysUserVO视图对象。
 *
 * @author Jerry
 * @date 2021-06-06
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
     * 用户显示名称。
     */
    private String showName;

    /**
     * 用户部门Id。
     */
    private Long deptId;

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
     * 创建者Id。
     */
    private Long createUserId;

    /**
     * 更新者Id。
     */
    private Long updateUserId;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 多对多用户岗位数据集合。
     */
    private List<Map<String, Object>> sysUserPostList;

    /**
     * 多对多用户角色数据集合。
     */
    private List<Map<String, Object>> sysUserRoleList;

    /**
     * 多对多用户数据权限数据集合。
     */
    private List<Map<String, Object>> sysDataPermUserList;

    /**
     * deptId 字典关联数据。
     */
    private Map<String, Object> deptIdDictMap;

    /**
     * userType 常量字典关联数据。
     */
    private Map<String, Object> userTypeDictMap;

    /**
     * userStatus 常量字典关联数据。
     */
    private Map<String, Object> userStatusDictMap;
}
