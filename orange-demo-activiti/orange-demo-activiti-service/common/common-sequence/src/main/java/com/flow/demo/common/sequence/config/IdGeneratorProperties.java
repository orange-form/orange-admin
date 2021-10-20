package com.flow.demo.common.sequence.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * common-sequence模块的配置类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@ConfigurationProperties(prefix = "sequence")
public class IdGeneratorProperties {

    /**
     * 基础版生成器所需的WorkNode参数值。仅当advanceIdGenerator为false时生效。
     */
    private Integer snowflakeWorkNode = 1;
}
