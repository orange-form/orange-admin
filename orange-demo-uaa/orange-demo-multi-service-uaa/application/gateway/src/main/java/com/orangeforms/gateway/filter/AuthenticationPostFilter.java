package com.orangeforms.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.ResponseResult;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.core.util.JwtUtil;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.RedisKeyUtil;
import com.orangeforms.gateway.config.ApplicationConfig;
import com.orangeforms.gateway.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 全局后处理过滤器。主要用于将用户的会话信息存到缓存服务器，以及在登出时清除缓存中的会话数据。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class AuthenticationPostFilter implements GlobalFilter, Ordered {

    @Autowired
    private ApplicationConfig appConfig;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();
        String refreshedToken =
                (String) exchange.getAttributes().get(appConfig.getRefreshedTokenHeaderKey());
        if (refreshedToken != null) {
            originalResponse.getHeaders().add(appConfig.getRefreshedTokenHeaderKey(), refreshedToken);
        }
        if (!originalRequest.getURI().getPath().equals(GatewayConstant.ADMIN_LOGIN_URL)
                && !originalRequest.getURI().getPath().equals(GatewayConstant.ADMIN_LOGIN_BY_UAA_URL)
                && !originalRequest.getURI().getPath().equals(GatewayConstant.ADMIN_LOGOUT_URL)) {
            return chain.filter(exchange);
        }
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @SuppressWarnings("unchecked")
            @Override
            public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> bodyData) {
                StringBuilder sb = new StringBuilder(128);
                sb.append("url: ")
                        .append(originalRequest.getURI().getPath())
                        .append(" -- status: ")
                        .append(getStatusCode());
                if (getStatusCode() != HttpStatus.OK) {
                    log.error(sb.toString());
                    return super.writeWith(bodyData);
                }
                if (!(bodyData instanceof Flux)) {
                    return super.writeWith(bodyData);
                }
                Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) bodyData;
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    // 读取完整的服务应答消息体。
                    String responseBody = readResponseBody(dataBuffers);
                    originalResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    // 先判断body中是否包含数据。
                    if (StringUtils.isBlank(responseBody)) {
                        sb.append(" -- Internal Error, no RESPONSE DATA returns !!");
                        log.error(sb.toString());
                        String errorMessage = "后台服务没有任何数据返回！";
                        responseBody = JSON.toJSONString(
                                ResponseResult.error(ErrorCodeEnum.SERVER_INTERNAL_ERROR, errorMessage));
                        byte[] uppedContent = new String(responseBody.getBytes(), StandardCharsets.UTF_8).getBytes();
                        originalResponse.getHeaders().setContentLength(uppedContent.length);
                        return bufferFactory.wrap(uppedContent);
                    }
                    // 处理登录和登出请求。
                    String result;
                    try {
                        result = doProcess(exchange, responseBody);
                    } catch (Exception e) {
                        setStatusCode(HttpStatus.BAD_REQUEST);
                        String errorMsg = "Server Internal Error";
                        sb.append(errorMsg);
                        log.error(sb.toString(), e);
                        result = JSON.toJSONString(
                                ResponseResult.error(ErrorCodeEnum.SERVER_INTERNAL_ERROR, errorMsg));
                    }
                    byte[] uppedContent = new String(result.getBytes(), StandardCharsets.UTF_8).getBytes();
                    originalResponse.getHeaders().setContentLength(uppedContent.length);
                    return bufferFactory.wrap(uppedContent);
                }));
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 返回过滤器在在调用链上的优先级。
     *
     * @return 数值越低，优先级越高。
     */
    @Override
    public int getOrder() {
        // -1 is response write filter, must be called before that
        return -2;
    }

    private String readResponseBody(List<? extends DataBuffer> dataBuffers) {
        int dataCount = 0;
        for (DataBuffer dataBuffer : dataBuffers) {
            dataCount += dataBuffer.readableByteCount();
        }
        byte[] allBytes = new byte[dataCount];
        int offset = 0;
        for (DataBuffer dataBuffer : dataBuffers) {
            int length = dataBuffer.readableByteCount();
            dataBuffer.read(allBytes, offset, length);
            DataBufferUtils.release(dataBuffer);
            offset += length;
        }
        return new String(allBytes, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private String doProcess(ServerWebExchange exchange, String responseBody) {
        // 这个解析出来的就是upms登录或登出接口返回的ResponseResult对象。
        ServerHttpRequest originalRequest = exchange.getRequest();
        if (originalRequest.getURI().getPath().equals(GatewayConstant.ADMIN_LOGIN_BY_UAA_URL)
                || originalRequest.getURI().getPath().equals(GatewayConstant.ADMIN_LOGIN_URL)) {
            // 处理登录服务的消息体，同时重构该消息体，并最终返回前端。
            ResponseResult<JSONObject> result = processLoginResponse(responseBody);
            return JSON.toJSONString(result);
        }
        if (originalRequest.getURI().getPath().equals(GatewayConstant.ADMIN_LOGOUT_URL)) {
            ResponseResult<Void> result = JSON.parseObject(responseBody, ResponseResult.class);
            if (result.isSuccess()) {
                String sessionId = (String) exchange.getAttributes().get(GatewayConstant.SESSION_ID_KEY_NAME);
                redissonClient.getBucket(RedisKeyUtil.makeSessionIdKey(sessionId)).deleteAsync();
                redissonClient.getSet(RedisKeyUtil.makeSessionPermIdKey(sessionId)).deleteAsync();
            }
            return responseBody;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ResponseResult<JSONObject> processLoginResponse(String responseBody) {
        ResponseResult<JSONObject> responseResult = JSON.parseObject(responseBody, ResponseResult.class);
        if (!responseResult.isSuccess()) {
            return responseResult;
        }
        JSONObject loginData = responseResult.getData();
        // 1. 先验证登陆服务器返回的应答数据是否正确
        JSONObject tokenData = loginData.getJSONObject(TokenData.REQUEST_ATTRIBUTE_NAME);
        ErrorCodeEnum errorCode = ErrorCodeEnum.SERVER_INTERNAL_ERROR;
        if (tokenData == null) {
            return ResponseResult.error(errorCode, "内部错误，用户登录令牌对象没有正确返回！");
        }
        Long userId = tokenData.getLong("userId");
        if (MyCommonUtil.isBlankOrNull(userId)) {
            return ResponseResult.error(errorCode, "内部错误，用户Id没有正确返回！");
        }
        Boolean isAdmin = tokenData.getBoolean("isAdmin");
        if (isAdmin == null) {
            return ResponseResult.error(errorCode, "内部错误，是否为管理员标记没有正确返回！");
        }
        String showName = tokenData.getString("showName");
        if (StringUtils.isBlank(showName)) {
            return ResponseResult.error(errorCode, "内部错误，用户显示名没有正确返回！");
        }
        String loginName = tokenData.getString("loginName");
        if (StringUtils.isBlank(loginName)) {
            return ResponseResult.error(errorCode, "内部错误，用户登录名没有正确返回！");
        }
        String sessionId = tokenData.getString("sessionId");
        if (StringUtils.isBlank(sessionId)) {
            return ResponseResult.error(errorCode, "内部错误，SESSION_ID没有正确返回！");
        }
        // 2. 生成sessionId并存放到token中
        Map<String, Object> claims = new HashMap<>(1);
        claims.put(GatewayConstant.SESSION_ID_KEY_NAME, sessionId);
        String token = JwtUtil.generateToken(claims, appConfig.getExpiration(), appConfig.getTokenSigningKey());
        // 3. 更新缓存
        String sessionIdKey = RedisKeyUtil.makeSessionIdKey(sessionId);
        String sessionData = JSON.toJSONString(tokenData, SerializerFeature.WriteNonStringValueAsString);
        RBucket<String> bucket = redissonClient.getBucket(sessionIdKey);
        bucket.set(sessionData);
        bucket.expire(appConfig.getSessionExpiredSeconds(), TimeUnit.SECONDS);
        // 3.2 sessionId -> permList 是set结构的缓存
        JSONArray permSet = loginData.getJSONArray("permSet");
        if (permSet != null) {
            String sessionPermKey = RedisKeyUtil.makeSessionPermIdKey(sessionId);
            RSet<String> redisPermSet = redissonClient.getSet(sessionPermKey);
            redisPermSet.addAll(permSet.stream().map(Object::toString).collect(Collectors.toSet()));
            redisPermSet.expire(appConfig.getSessionExpiredSeconds(), TimeUnit.SECONDS);
        }
        // 4. 构造返回给用户的应答，将加密后的令牌返回给前端。
        loginData.put(TokenData.REQUEST_ATTRIBUTE_NAME, token);
        // 5. 这里需要移除权限资源集合的数据，验证在后端进行，无需返回给前端。
        loginData.remove("permSet");
        return ResponseResult.success(loginData);
    }
}
