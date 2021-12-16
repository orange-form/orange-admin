package com.orangeforms.common.datafilter.config;

import com.orangeforms.common.datafilter.interceptor.DataFilterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 添加数据过滤相关的拦截器。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Configuration
public class DataFilterWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DataFilterInterceptor()).addPathPatterns("/**");
    }
}
