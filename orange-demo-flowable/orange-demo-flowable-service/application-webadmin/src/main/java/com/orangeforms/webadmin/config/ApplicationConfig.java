package com.orangeforms.webadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 应用程序自定义的程序属性配置文件。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {

    /**
     * token的Http Request Header的key
     */
    private String tokenHeaderKey;
    /**
     * token在过期之前，但是已经需要被刷新时，response返回的header信息的key。
     */
    private String refreshedTokenHeaderKey;
    /**
     * token 加密用的密钥，该值的长度最少10个字符(过短会报错)。
     */
    private String tokenSigningKey;
    /**
     * 令牌的过期时间，单位毫秒
     */
    private Long expiration;
    /**
     * 用户密码被重置之后的缺省密码
     */
    private String defaultUserPassword;
    /**
     * 上传文件的基础目录
     */
    private String uploadFileBaseDir;
    /**
     * 授信ip列表，没有填写表示全部信任。多个ip之间逗号分隔，如: http://10.10.10.1:8080,http://10.10.10.2:8080
     */
    private String credentialIpList;
    /**
     * Session的用户权限在Redis中的过期时间(秒)。
     * 缺省值是 one day
     */
    private int sessionExpiredSeconds = 86400;
}
