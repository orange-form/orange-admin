package com.orangeforms.uaaadmin.interceptor;

import com.alibaba.fastjson.JSON;
import com.orangeforms.common.core.annotation.NoAuthInterface;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.ResponseResult;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.core.util.ApplicationContextHolder;
import com.orangeforms.common.core.util.JwtUtil;
import com.orangeforms.common.core.util.RedisKeyUtil;
import com.orangeforms.uaaadmin.config.ApplicationConfig;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录用户Token验证、生成和权限验证的拦截器。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final ApplicationConfig appConfig =
            ApplicationContextHolder.getBean("applicationConfig");
    private final RedissonClient redissonClient =
            ApplicationContextHolder.getBean(RedissonClient.class);
    private final String operatorAdminUrlPrefix = "/admin/uaaadmin/sysUaaOperator";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String url = request.getRequestURI();
        // 如果接口方法标记NoAuthInterface注解，可以直接跳过Token鉴权验证，这里主要为了测试接口方便
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.getBeanType().getAnnotation(NoAuthInterface.class) != null
                    || hm.getMethodAnnotation(NoAuthInterface.class) != null) {
                return true;
            }
        }
        String token = request.getHeader(appConfig.getTokenHeaderKey());
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(appConfig.getTokenHeaderKey());
        }
        Claims c = JwtUtil.parseToken(token, appConfig.getTokenSigningKey());
        if (JwtUtil.isNullOrExpired(c)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            this.outputResponseMessage(response,
                    ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN, "用户会话已过期或尚未登录，请重新登录！"));
            return false;
        }
        String sessionId = (String) c.get("sessionId");
        RBucket<String> bucket = redissonClient.getBucket(RedisKeyUtil.makeSessionIdKey(sessionId));
        String tokenString = bucket.get();
        if (tokenString == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            this.outputResponseMessage(response,
                    ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN, "用户会话已失效，请重新登录！"));
            return false;
        }
        TokenData tokenData = JSON.parseObject(tokenString, TokenData.class);
        TokenData.addToRequest(tokenData);
        // 操作员管理的URL接口，只有管理员可以访问。
        if (!tokenData.getIsAdmin() && StringUtils.startsWithIgnoreCase(url, this.operatorAdminUrlPrefix)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            this.outputResponseMessage(response,
                    ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION));
            return false;
        }
        if (JwtUtil.needToRefresh(c)) {
            String refreshedToken = JwtUtil.generateToken(c, appConfig.getExpiration(), appConfig.getTokenSigningKey());
            response.addHeader(appConfig.getRefreshedTokenHeaderKey(), refreshedToken);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 这里需要空注解，否则sonar会不happy。
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 这里需要空注解，否则sonar会不happy。
    }

    private void outputResponseMessage(HttpServletResponse response, ResponseResult<Object> respObj) {
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            log.error("Failed to call OutputResponseMessage.", e);
            return;
        }
        response.setContentType("application/json; charset=utf-8");
        out.print(JSON.toJSONString(respObj));
        out.flush();
        out.close();
    }
}
