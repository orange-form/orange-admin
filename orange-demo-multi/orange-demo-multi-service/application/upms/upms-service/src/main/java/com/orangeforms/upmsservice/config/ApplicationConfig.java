package com.orangeforms.upmsservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 应用程序自定义的程序属性配置文件。
 * NOTE: 和multiDataSource相关的配置没有包含进来，因为涉及到条件属性，所以由其相关的配置对象自己处理。
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
     * 用户密码被重置之后的缺省密码
     */
    private String defaultUserPassword;
    /**
     * 上传文件的基础目录
     */
    private String uploadFileBaseDir;
    /**
     * 每个微服务的url目录上下文，如(/admin/upms)，通常和网关的路由目录一致。
     */
    private String serviceContextPath;
    /**
     * 是否忽略远程调用中出现的任何错误，包括逻辑异常和系统异常。
     * 通常在调试和测试阶段设置为false，以便及时发现问题。
     */
    private Boolean ignoreRpcError;
    /**
     * Session的数据权限缓存时长(单位：秒)。
     */
    private Integer dataPermExpiredSeconds;
}
