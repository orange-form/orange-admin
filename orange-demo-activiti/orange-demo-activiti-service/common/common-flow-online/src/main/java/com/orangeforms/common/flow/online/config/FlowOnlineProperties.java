package com.orangeforms.common.flow.online.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 在线表单工作流模块的配置对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@ConfigurationProperties(prefix = "common-flow-online")
public class FlowOnlineProperties {

    /**
     * 在线表单的URL前缀。
     */
    private String urlPrefix;
}
