package com.orange.demo.common.core.object;

import com.alibaba.fastjson.JSON;
import com.orange.demo.common.core.util.ContextUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 基于Jwt，用于前后端传递的令牌对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@ToString
@Slf4j
public class TokenData {

    /**
     * 在HTTP Request对象中的属性键。
     */
    public static final String REQUEST_ATTRIBUTE_NAME = "tokenData";
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
     * 用户的部门岗位Id。多个岗位之间逗号分隔。仅当系统支持岗位时有值。
     */
    private String deptPostIds;
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
     * 设备类型。参考 AppDeviceType。
     */
    private Integer deviceType;
    /**
     * 标识不同登录的会话Id。
     */
    private String sessionId;
    /**
     * 访问uaa的授权token。
     * 仅当系统支持uaa时可用，否则可以直接忽略该字段。保留该字段是为了保持单体和微服务通用代码部分的兼容性。
     */
    private String uaaAccessToken;
    /**
     * 数据库路由键(仅当水平分库时使用)。
     */
    private Integer datasourceRouteKey;
    /**
     * 登录IP。
     */
    private String loginIp;
    /**
     * 登录时间。
     */
    private Date loginTime;

    /**
     * 将令牌对象添加到Http请求对象。
     *
     * @param tokenData 令牌对象。
     */
    public static void addToRequest(TokenData tokenData) {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        request.setAttribute(TokenData.REQUEST_ATTRIBUTE_NAME, tokenData);
    }

    /**
     * 从Http Request对象中获取令牌对象。
     *
     * @return 令牌对象。
     */
    public static TokenData takeFromRequest() {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        TokenData tokenData = (TokenData) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
        if (tokenData != null) {
            return tokenData;
        }
        String token = request.getHeader(REQUEST_ATTRIBUTE_NAME);
        if (StringUtils.isNotBlank(token)) {
            tokenData = JSON.parseObject(token, TokenData.class);
        } else {
            token = request.getParameter(REQUEST_ATTRIBUTE_NAME);
            if (StringUtils.isNotBlank(token)) {
                tokenData = JSON.parseObject(token, TokenData.class);
            }
        }
        if (tokenData != null) {
            try {
                tokenData.showName = URLDecoder.decode(tokenData.showName, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                log.error("Failed to call TokenData.takeFromRequest", e);
            }
            addToRequest(tokenData);
        }
        return tokenData;
    }
}
