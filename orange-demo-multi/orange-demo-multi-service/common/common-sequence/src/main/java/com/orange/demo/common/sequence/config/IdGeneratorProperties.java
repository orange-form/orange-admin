package com.orange.demo.common.sequence.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * common-sequence模块的配置类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@ConfigurationProperties(prefix = "sequence")
public class IdGeneratorProperties {

    /**
     * 是否使用基于美团Leaf的分布式Id生成器。
     */
    private Boolean advanceIdGenerator = false;
    /**
     * 基础版生成器所需的WorkNode参数值。仅当advanceIdGenerator为false时生效。
     */
    private Integer snowflakeWorkNode = 1;
    /**
     * zk的地址。多个ip和端口之间逗号分隔。仅当advanceIdGenerator为true时生效。
     * 如：10.1.1.2:2181;10.1.1.3:2181。
     */
    private String zkAddress;
    /**
     * 用于识别同一主机(ip相同)不同服务的端口号。与本机的ip一起构成zk中标识不同服务实例的key值。
     * 仅当advanceIdGenerator为true时生效。
     */
    private Integer idPort;
    /**
     * zk中生成WorkNode的路径。不同的业务可以使用不同的路径，以免冲突。
     * 仅当advanceIdGenerator为true时生效。
     */
    private String zkPath;
}
