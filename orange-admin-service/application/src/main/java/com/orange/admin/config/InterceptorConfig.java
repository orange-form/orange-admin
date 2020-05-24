package com.orange.admin.config;

import com.orange.admin.interceptor.AuthenticationInterceptor;
import com.orange.admin.common.biz.interceptor.AccessInterceptor;
import com.orange.admin.common.biz.interceptor.MyRequestArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 所有的项目拦截器都在这里集中配置。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AuthenticationInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(new AccessInterceptor()).addPathPatterns("/**");
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		// 添加MyRequestBody参数解析器
		argumentResolvers.add(new MyRequestArgumentResolver());
	}

	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
	    return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    converters.add(responseBodyConverter());
    }
}
