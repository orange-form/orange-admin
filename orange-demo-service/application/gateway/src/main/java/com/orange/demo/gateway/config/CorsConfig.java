package com.orange.demo.gateway.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 跨域信任配置类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter(ApplicationConfig appConfig) {
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        CorsConfiguration config = new CorsConfiguration();
        if (StringUtils.isNotBlank(appConfig.getCredentialIpList())) {
            String[] credentialIpList = StringUtils.split(appConfig.getCredentialIpList(), ",");
            if (credentialIpList.length > 0) {
                for (String ip : credentialIpList) {
                    config.addAllowedOrigin(ip);
                }
            }
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            config.addExposedHeader(appConfig.getRefreshedTokenHeaderKey());
            config.setAllowCredentials(true);
            configSource.registerCorsConfiguration("/**", config);
        }
        return new CorsWebFilter(configSource);
    }
}
