package com.flow.demo.common.core.config;

import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tomcat配置对象。当前配置禁用了PUT和DELETE方法，防止渗透攻击。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Configuration
public class TomcatConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addContextCustomizers(context -> {
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setUserConstraint("CONFIDENTIAL");
            SecurityCollection collection = new SecurityCollection();
            collection.addPattern("/*");
            collection.addMethod("HEAD");
            collection.addMethod("PUT");
            collection.addMethod("PATCH");
            collection.addMethod("DELETE");
            collection.addMethod("TRACE");
            collection.addMethod("COPY");
            collection.addMethod("SEARCH");
            collection.addMethod("PROPFIND");
            securityConstraint.addCollection(collection);
            context.addConstraint(securityConstraint);
        });
        return factory;
    }
}
