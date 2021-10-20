package com.orange.demo.common.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 操作日志的配置类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@ConfigurationProperties(prefix = "common-log.operation-log")
public class OperationLogProperties {

    /**
     * 是否采集操作日志。
     */
    private boolean enabled = true;
}
