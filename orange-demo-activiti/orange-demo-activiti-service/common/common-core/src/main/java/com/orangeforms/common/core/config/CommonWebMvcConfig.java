package com.orangeforms.common.core.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.orangeforms.common.core.interceptor.MyRequestArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 所有的项目拦截器、参数解析器、消息对象转换器都在这里集中配置。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Configuration
public class CommonWebMvcConfig implements WebMvcConfigurer {

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

	@Bean
	public FastJsonHttpMessageConverter fastJsonHttpMessageConverters() {
	FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
	List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
		fastConverter.setSupportedMediaTypes(supportedMediaTypes);
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(
				SerializerFeature.PrettyFormat,
				SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.IgnoreNonFieldGetter);
		fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		fastConverter.setFastJsonConfig(fastJsonConfig);
		return fastConverter;
	}

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    converters.add(responseBodyConverter());
		converters.add(fastJsonHttpMessageConverters());
    }
}
