package com.orange.demo.gateway.filter;

import com.orange.demo.common.core.constant.ApplicationConstant;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.gateway.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 链路日志前置过虑器。
 * 为整个链路生成唯一的traceId，并存储在Request Head中。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Slf4j
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = MyCommonUtil.generateUuid();
        // 分别记录traceId和执行开始时间。
        exchange.getAttributes().put(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        exchange.getAttributes().put(GatewayConstant.START_TIME_ATTRIBUTE, System.currentTimeMillis());
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header(
                ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        MDC.put(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        log.info("开始请求，app={gateway}, url={}", exchange.getRequest().getURI().getPath());
        return chain.filter(mutableExchange);
    }

    /**
     * 返回过滤器在在调用链上的优先级。
     *
     * @return 数值越低，优先级越高。
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 9900;
    }
}
