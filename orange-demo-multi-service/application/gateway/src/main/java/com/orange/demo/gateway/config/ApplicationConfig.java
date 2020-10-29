package com.orange.demo.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 网关业务配置类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {

    /**
     * token加密用的密钥，该值的长度最少10个字符(过短会报错)。
     */
    private String tokenSigningKey;
    /**
     * 客户端或者浏览器在提交http请求时，携带token的header name，如 Authorization
     */
    private String tokenHeaderKey;
    /**
     * 令牌Token在被刷新之后，服务器Http应答的header name，客户端或浏览器需要保存并替换原有的token，用于下次发送时携带
     */
    private String refreshedTokenHeaderKey;
    /**
     * 令牌的过期时间，单位毫秒
     */
    private Long expiration;
    /**
     * 授信ip列表，没有填写表示全部信任。多个ip之间逗号分隔，如: http://10.10.10.1:8080,http://10.10.10.2:8080
     */
    private String credentialIpList;
    /**
     * Session在Redis中的过期时间(秒)。
     * 缺省值是 one day + 60s
     */
    private int sessionIdRedisExpiredSeconds = 86460;
    /**
     * Session的用户权限在Redis中的过期时间(秒)。
     * 缺省值是 one day
     */
    private int permRedisExpiredSeconds = 86400;
    /**
     * 基于完全等于(equals)判定规则的白名单地址集合，过滤效率高于whitelistUrlPattern。
     */
    private Set<String> whitelistUrl;
    /**
     * 基于Ant Pattern模式判定规则的白名单地址集合。如：/aa/**。
     */
    private Set<String> whitelistUrlPattern;
}
