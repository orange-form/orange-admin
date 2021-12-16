package com.orangeforms.common.online.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 在线表单的配置对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@ConfigurationProperties(prefix = "common-online")
public class OnlineProperties {

    /**
     * 仅以该前缀开头的数据表才会成为动态表单的候选数据表，如: zz_。如果为空，则所有表均可被选。
     */
    private String tablePrefix;

    /**
     * 在线表单业务操作的URL前缀。
     */
    private String operationUrlPrefix;

    /**
     * 上传文件的根路径。
     */
    private String uploadFileBaseDir;
}
