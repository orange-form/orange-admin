package com.orange.demo.gateway;

import com.orange.demo.common.core.util.ApplicationContextHolder;
import com.orange.demo.gateway.filter.AuthenticationPostFilter;
import com.orange.demo.gateway.filter.AuthenticationPreFilter;
import com.orange.demo.gateway.filter.RequestLogFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网关服务启动类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@SpringCloudApplication
public class GatewayApplication {

    @RestController
    @RequestMapping("/fallback")
    static class FallbackController {
        @GetMapping("")
        public String fallback() {
            return "GATEWAY FALLBACK!!!";
        }
    }

    @Bean
    public AuthenticationPreFilter authenticationPreFilter() {
        return new AuthenticationPreFilter();
    }

    @Bean
    public AuthenticationPostFilter authenticationPostFilter() {
        return new AuthenticationPostFilter();
    }

    @Bean
    public RequestLogFilter requestLogPreFilter() {
        return new RequestLogFilter();
    }

    @Bean
    ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
