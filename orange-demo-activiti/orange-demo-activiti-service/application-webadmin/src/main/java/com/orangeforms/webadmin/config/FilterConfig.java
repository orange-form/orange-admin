package com.orangeforms.webadmin.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;

/**
 * 这里主要配置Web的各种过滤器和监听器等Servlet容器组件。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Configuration
public class FilterConfig {

    /**
     * 配置Ajax跨域过滤器。
     */
    @Bean
    public CorsFilter corsFilterRegistration(ApplicationConfig applicationConfig) {
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        if (StringUtils.isNotBlank(applicationConfig.getCredentialIpList())) {
            String[] credentialIpList = StringUtils.split(applicationConfig.getCredentialIpList(), ",");
            if (credentialIpList.length > 0) {
                for (String ip : credentialIpList) {
                    corsConfiguration.addAllowedOrigin(ip);
                }
            }
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addAllowedMethod("*");
            corsConfiguration.addExposedHeader(applicationConfig.getRefreshedTokenHeaderKey());
            corsConfiguration.setAllowCredentials(true);
            configSource.registerCorsConfiguration("/**", corsConfiguration);
        }
        return new CorsFilter(configSource);
    }

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
