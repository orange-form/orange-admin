package com.orange.demo.common.core.config;

import com.orange.demo.common.core.constant.ApplicationConstant;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.util.ContextUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * FeignClient的配置对象。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(TokenData.REQUEST_ATTRIBUTE_NAME,
                ContextUtil.getHttpRequest().getHeader(TokenData.REQUEST_ATTRIBUTE_NAME));
        requestTemplate.header(ApplicationConstant.HTTP_HEADER_TRACE_ID,
                ContextUtil.getHttpRequest().getHeader(ApplicationConstant.HTTP_HEADER_TRACE_ID));
    }
}
