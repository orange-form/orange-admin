package com.orangeforms.uaaauth.config;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.io.IOException;

/**
 * OAuth2AccessToken 接口对象的专有序列化转换器。如果不指定该转换器，并采用缺省的Fastjson，
 * /oauth/token返回的对象参数格式不符合oauth2的标准。
 * 由于OAuth2AccessToken接口对象中的每一个字段，都基于jackson指定了不同的序列化方式，因此，
 * 这里我们针对OAuth2AccessToken，仍然保留使用jackson的序列化方式。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@SuppressWarnings("NullableProblems")
public class AuthHttpMessageConverter extends AbstractHttpMessageConverter<DefaultOAuth2AccessToken> {

    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

    public AuthHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.equals(DefaultOAuth2AccessToken.class);
    }

    @Override
    protected DefaultOAuth2AccessToken readInternal(
            Class<? extends DefaultOAuth2AccessToken> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException(
                "This converter is only used for converting DefaultOAuth2AccessToken to json.");
    }

    @Override
    protected void writeInternal(DefaultOAuth2AccessToken accessToken, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        mappingJackson2HttpMessageConverter.write(accessToken, MediaType.APPLICATION_JSON, outputMessage);
    }
}