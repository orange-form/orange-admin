package com.orange.demo.gateway.constant;

/**
 * 网关业务相关的常量对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class GatewayConstant {

    /**
     * 请求进入网关的开始时间。
     */
    public static final String START_TIME_ATTRIBUTE = "startTime";

    /**
     * 登录URL。
     */
    public static final String ADMIN_LOGIN_URL = "/admin/upms/login/doLogin";

    /**
     * 登出URL。
     */
    public static final String ADMIN_LOGOUT_URL = "/admin/upms/login/doLogout";

    /**
     * sessionId的键名称。
     */
    public static final String SESSION_ID_KEY_NAME = "sessionId";

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private GatewayConstant() {
    }
}
