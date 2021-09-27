package com.flow.demo.common.core.object;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 在线登录用户信息。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@ToString
@Slf4j
public class LoginUserInfo {

    /**
     * 用户Id。
     */
    private Long userId;
    /**
     * 用户所在部门Id。
     * 仅当系统支持uaa时可用，否则可以直接忽略该字段。保留该字段是为了保持单体和微服务通用代码部分的兼容性。
     */
    private Long deptId;
    /**
     * 租户Id。
     * 仅当系统支持uaa时可用，否则可以直接忽略该字段。保留该字段是为了保持单体和微服务通用代码部分的兼容性。
     */
    private Long tenantId;
    /**
     * 是否为超级管理员。
     */
    private Boolean isAdmin;
    /**
     * 用户登录名。
     */
    private String loginName;
    /**
     * 用户显示名称。
     */
    private String showName;
    /**
     * 标识不同登录的会话Id。
     */
    private String sessionId;
    /**
     * 登录IP。
     */
    private String loginIp;
    /**
     * 登录时间。
     */
    private Date loginTime;
    /**
     * 登录设备类型。
     */
    private Integer deviceType;
}
