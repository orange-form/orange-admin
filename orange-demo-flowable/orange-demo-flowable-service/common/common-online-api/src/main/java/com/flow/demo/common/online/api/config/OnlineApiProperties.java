package com.flow.demo.common.online.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 在线表单API的配置对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@ConfigurationProperties(prefix = "common-online-api")
public class OnlineApiProperties {

    /**
     * 在线表单的URL前缀。
     */
    private String urlPrefix;

    /**
     * 在线表单查看权限的URL列表。
     */
    private List<String> viewUrlList;

    /**
     * 在线表单编辑权限的URL列表。
     */
    private List<String> editUrlList;
}
