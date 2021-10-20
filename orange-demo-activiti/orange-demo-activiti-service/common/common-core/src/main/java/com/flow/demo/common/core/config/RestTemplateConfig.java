package com.flow.demo.common.core.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * RestTemplate连接池配置对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Configuration
public class RestTemplateConfig {
    private static final int MAX_TOTAL_CONNECTION = 50;
    private static final int MAX_CONNECTION_PER_ROUTE = 20;
    private static final int CONNECTION_TIMEOUT = 20000;
    private static final int READ_TIMEOUT = 30000;

    @Bean
    @ConditionalOnMissingBean({RestOperations.class, RestTemplate.class})
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(createFactory());
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.removeIf(
                c -> c instanceof StringHttpMessageConverter || c instanceof MappingJackson2HttpMessageConverter);
        messageConverters.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        messageConverters.add(new FastJsonHttpMessageConverter());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // 防止400+和500等错误被直接抛出异常，这里避开了缺省处理方式，所有的错误均交给业务代码处理。
            }
        });
        return restTemplate;
    }

    private ClientHttpRequestFactory createFactory() {
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(MAX_TOTAL_CONNECTION)
                .setMaxConnPerRoute(MAX_CONNECTION_PER_ROUTE)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(READ_TIMEOUT);
        factory.setConnectTimeout(CONNECTION_TIMEOUT);
        return factory;
    }
}