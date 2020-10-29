package com.orange.demo.common.core.object;

import com.orange.demo.common.core.util.ContextUtil;
import lombok.Data;
import lombok.ToString;

import javax.servlet.http.HttpServletRequest;

/**
 * 基于Jwt，用于前后端传递的令牌对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@ToString
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
     * 是否为超级管理员。
     */
    private Boolean isAdmin;
    /**
     * 用户显示名称。
     */
    private String showName;
    /**
     * 标识不同登录的会话Id。
     */
    private String sessionId;

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
        return (TokenData) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
    }
}
