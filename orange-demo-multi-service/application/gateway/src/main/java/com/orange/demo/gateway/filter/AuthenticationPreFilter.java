package com.orange.demo.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.util.JwtUtil;
import com.orange.demo.common.core.util.RedisKeyUtil;
import com.orange.demo.common.core.util.IpUtil;
import com.orange.demo.gateway.config.ApplicationConfig;
import com.orange.demo.gateway.constant.GatewayConstant;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 全局前处理过滤器。主要用于用户操作权限验证。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class AuthenticationPreFilter implements GlobalFilter, Ordered {

    @Autowired
    private ApplicationConfig appConfig;
    @Autowired
    private RedissonClient redissonClient;
    /**
     * Ant Pattern模式的白名单地址匹配器。
     */
    private final AntPathMatcher antMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String url = request.getURI().getPath();
        // 判断是否为白名单请求，以及一些内置不需要验证的请求。(登录请求也包含其中)。
        if (this.shouldNotFilter(url)) {
            return chain.filter(exchange);
        }
        String token = this.getTokenFromRequest(request);
        Claims c = JwtUtil.parseToken(token, appConfig.getTokenSigningKey());
        if (JwtUtil.isNullOrExpired(c)) {
            log.warn("EXPIRED request [{}] from REMOTE-IP [{}].", url, IpUtil.getRemoteIpAddress(request));
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] responseBody = JSON.toJSONString(ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN,
                    "用户登录已过期或尚未登录，请重新登录！")).getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Flux.just(response.bufferFactory().wrap(responseBody)));
        }
        // 这里判断是否需要定时刷新token
        if (JwtUtil.needToRefresh(c)) {
            exchange.getAttributes().put(appConfig.getRefreshedTokenHeaderKey(),
                    JwtUtil.generateToken(c, appConfig.getExpiration(), appConfig.getTokenSigningKey()));
        }
        // 先基于sessionId获取userInfo
        String sessionId = (String) c.get(GatewayConstant.SESSION_ID_KEY_NAME);
        String sessionIdKey = RedisKeyUtil.makeSessionIdKeyForRedis(sessionId);
        RBucket<String> sessionData = redissonClient.getBucket(sessionIdKey);
        JSONObject tokenData = null;
        if (sessionData.isExists()) {
            tokenData = JSON.parseObject(sessionData.get());
        }
        if (tokenData == null) {
            log.warn("UNAUTHORIZED request [{}] from REMOTE-IP [{}] because no sessionId exists in redis.",
                    url, IpUtil.getRemoteIpAddress(request));
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] responseBody = JSON.toJSONString(ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN,
                    "用户会话已失效，请重新登录！")).getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Flux.just(response.bufferFactory().wrap(responseBody)));
        }
        String userId = tokenData.getString("userId");
        if (StringUtils.isBlank(userId)) {
            log.warn("UNAUTHORIZED request [{}] from REMOTE-IP [{}] because userId is empty in redis.",
                    url, IpUtil.getRemoteIpAddress(request));
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] responseBody = JSON.toJSONString(ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN,
                    "用户登录验证信息已过期，请重新登录！")).getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Flux.just(response.bufferFactory().wrap(responseBody)));
        }
        String showName = tokenData.getString("showName");
        // 因为http header中不支持中文传输，所以需要编码。
        try {
            showName = URLEncoder.encode(showName, StandardCharsets.UTF_8.name());
            tokenData.put("showName", showName);
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to call AuthenticationPreFilter.filter.", e);
        }
        boolean isAdmin = tokenData.getBoolean("isAdmin");
        if (Boolean.FALSE.equals(isAdmin) && !this.hasPermission(redissonClient, sessionId, url)) {
            log.warn("FORBIDDEN request [{}] from REMOTE-IP [{}] for USER [{} -- {}] no perm!",
                    url, IpUtil.getRemoteIpAddress(request), userId, showName);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] responseBody = JSON.toJSONString(ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION,
                    "用户对该URL没有访问权限，请核对！")).getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Flux.just(response.bufferFactory().wrap(responseBody)));
        }
        // 将session中关联的用户信息，添加到当前的Request中。转发后，业务服务可以根据需要自定读取。
        tokenData.put("sessionId", sessionId);
        exchange.getAttributes().put(GatewayConstant.SESSION_ID_KEY_NAME, sessionId);
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header(
                TokenData.REQUEST_ATTRIBUTE_NAME, tokenData.toJSONString()).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);
    }

    /**
     * 返回过滤器在在调用链上的优先级。
     *
     * @return 数值越低，优先级越高。
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 10000;
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(appConfig.getTokenHeaderKey());
        if (StringUtils.isBlank(token)) {
            token = request.getQueryParams().getFirst(appConfig.getTokenHeaderKey());
        }
        return token;
    }

    private boolean hasPermission(RedissonClient redissonClient, String sessionId, String url) {
        // 对于退出登录操作，不需要进行权限验证，仅仅确认是已经登录的合法用户即可。
        if (url.equals(GatewayConstant.ADMIN_LOGOUT_URL)) {
            return true;
        }
        String permKey = RedisKeyUtil.makeSessionPermIdKeyForRedis(sessionId);
        return redissonClient.getSet(permKey).contains(url);
    }

    /**
     * 判断当前请求的url是否为配置中的白名单地址。以及一些内置的不需要登录即可访问的url。
     * @param url 请求的url。
     * @return 是返回true，否则false。
     */
    private boolean shouldNotFilter(String url) {
        // 这里过滤和swagger相关的url
        if (url.endsWith("/v2/api-docs") || url.endsWith("/v2/api-docs-ext")) {
            return true;
        }
        if (url.equals(GatewayConstant.ADMIN_LOGIN_URL)) {
            return true;
        }
        // 先过滤直接匹配的白名单url。
        if (CollectionUtils.isNotEmpty(appConfig.getWhitelistUrl())) {
            if (appConfig.getWhitelistUrl().contains(url)) {
                return true;
            }
        }
        // 过滤ant pattern模式的白名单url。
        if (CollectionUtils.isNotEmpty(appConfig.getWhitelistUrlPattern())) {
            for (String urlPattern : appConfig.getWhitelistUrlPattern()) {
                if (antMatcher.match(urlPattern, url)) {
                    return true;
                }
            }
        }
        return false;
    }
}
