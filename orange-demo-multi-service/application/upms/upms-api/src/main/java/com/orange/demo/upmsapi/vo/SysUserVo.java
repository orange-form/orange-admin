package com.orange.demo.upmsapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * SysUserVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("SysUserVO实体对象")
@Data
public class SysUserVo {

    /**
     * 用户Id。
     */
    @ApiModelProperty(value = "用户Id")
    private Long userId;

    /**
     * 登录用户名。
     */
    @ApiModelProperty(value = "登录用户名")
    private String loginName;

    /**
     * 用户显示名称。
     */
    @ApiModelProperty(value = "用户显示名称")
    private String showName;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    @ApiModelProperty(value = "用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)")
    private Integer userType;

    /**
     * 用户头像的Url。
     */
    @ApiModelProperty(value = "用户头像的Url")
    private String headImageUrl;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
    @ApiModelProperty(value = "用户状态(0: 正常 1: 锁定)")
    private Integer userStatus;

    /**
     * 创建用户Id。
     */
    @ApiModelProperty(value = "创建用户Id")
    private Long createUserId;

    /**
     * 更新者Id。
     */
    @ApiModelProperty(value = "更新者Id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 多对多用户角色数据集合。
     */
    @ApiModelProperty(value = "多对多用户角色数据集合")
    private List<Map<String, Object>> sysUserRoleList;

    /**
     * userType 常量字典关联数据。
     */
    @ApiModelProperty(value = "userType 常量字典关联数据")
    private Map<String, Object> userTypeDictMap;

    /**
     * userStatus 常量字典关联数据。
     */
    @ApiModelProperty(value = "userStatus 常量字典关联数据")
    private Map<String, Object> userStatusDictMap;
}
