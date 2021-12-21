package com.orangeforms.uaaadmin.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 这里主要配置Web的各种过滤器和监听器等Servlet容器组件。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Configuration
public class CorsConfig {

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
}
