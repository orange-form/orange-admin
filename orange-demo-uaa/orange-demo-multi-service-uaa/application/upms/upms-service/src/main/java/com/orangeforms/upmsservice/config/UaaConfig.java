package com.orangeforms.upmsservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * UAA授权应用服务的配置文件。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application.uaa")
public class UaaConfig {
    /**
     * uaa的授权服务的主机名。
     */
    private String uaaBaseUri;
    /**
     * uaa登录成功后的回调地址，需要和UAA授权服务器中注册的完全匹配。
     */
    private String loginUaaRedirectUri;
    /**
     * uaa登出成功后的回调地址。
     */
    private String logoutUaaRedirectUri;
    /**
     * 应用Id。需要和UAA授权服务器中注册的完全匹配。
     */
    private String clientId;
    /**
     * 应用密码。需要和UAA授权服务器中注册的完全匹配。
     */
    private String clientSecret;
}
