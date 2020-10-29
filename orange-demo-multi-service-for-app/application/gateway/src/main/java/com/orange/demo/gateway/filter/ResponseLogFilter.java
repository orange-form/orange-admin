package com.orange.demo.gateway.filter;

import com.orange.demo.common.core.constant.ApplicationConstant;
import com.orange.demo.gateway.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 链路日志后置过虑器。
 * 将整个链路的traceId存储在Response Head中，并返回给前端，便于问题定位。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class ResponseLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 下面两个属性，都是在RequestLogFilter过滤器中设置的。
        String traceId = exchange.getAttribute(ApplicationConstant.HTTP_HEADER_TRACE_ID);
        Long startTime = exchange.getAttribute(GatewayConstant.START_TIME_ATTRIBUTE);
        if (StringUtils.isNotBlank(traceId)) {
            MDC.put(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
            exchange.getResponse().getHeaders().add(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        }
        long elapse = 0;
        if (startTime != null) {
            elapse = System.currentTimeMillis() - startTime;
        }
        log.info("请求完成, app={gateway}, url={}，elapse={}", exchange.getRequest().getURI().getPath(), elapse);
        return chain.filter(exchange);
    }

    /**
     * 返回过滤器在在调用链上的优先级。
     *
     * @return 数值越低，优先级越高。
     */
    @Override
    public int getOrder() {
        // -1 is response write filter, must be called before that
        return -10;
    }
}
