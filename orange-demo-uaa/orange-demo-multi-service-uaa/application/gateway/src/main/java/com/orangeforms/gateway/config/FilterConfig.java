package com.orangeforms.gateway.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;

/**
 * Web通用过滤器配置类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> characterEncodingFilterRegistration() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>(
                new org.springframework.web.filter.CharacterEncodingFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("encoding", StandardCharsets.UTF_8.name());
        // forceEncoding强制response也被编码，另外即使request中已经设置encoding，forceEncoding也会重新设置
        filterRegistrationBean.addInitParameter("forceEncoding", "true");
        filterRegistrationBean.setAsyncSupported(true);
        return filterRegistrationBean;
    }
}
