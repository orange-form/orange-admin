package com.orangeforms.uaaauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 为OAuth2AccessToken 接口对象设定专有的序列化转换器。如果不指定该转换器，并采用缺省的Fastjson，
 * /oauth/token返回的对象参数格式不符合oauth2的标准。
 * 由于OAuth2AccessToken接口对象中的每一个字段，都基于jackson指定了不同的序列化方式，因此，
 * 这里我们针对OAuth2AccessToken，仍然保留使用jackson的序列化方式。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Configuration
public class AuthWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        AuthHttpMessageConverter authHttpMessageConverter = new AuthHttpMessageConverter();
        converters.add(0, authHttpMessageConverter);
    }
}
