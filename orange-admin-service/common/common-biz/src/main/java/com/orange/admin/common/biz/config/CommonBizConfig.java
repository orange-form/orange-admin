package com.orange.admin.common.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 应用程序自定义的通用属性配置文件。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application.common-biz")
public class CommonBizConfig {

    /**
     * Snowflake计算主键Id时所需的WorkNode参数值。
     */
    private Integer snowflakeWorkNode;
}
