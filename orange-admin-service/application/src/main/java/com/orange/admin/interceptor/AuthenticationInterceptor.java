package com.orange.admin.interceptor;

import com.orange.admin.config.CacheConfig;
import com.orange.admin.config.ApplicationConfig;
import com.orange.admin.upms.model.SysPermWhitelist;
import com.orange.admin.upms.service.SysPermWhitelistService;
import com.orange.admin.upms.service.SysPermService;
import com.orange.admin.common.core.annotation.NoAuthInterface;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.object.TokenData;
import com.orange.admin.common.core.util.ApplicationContextHolder;
import com.orange.admin.common.core.util.JwtUtil;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户Token验证、生成和权限验证的拦截器。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private ApplicationConfig applicationConfig =
            ApplicationContextHolder.getBean("applicationConfig");

    private CacheManager cacheManager =
            ApplicationContextHolder.getBean("cacheManager");

    private SysPermService sysPermService =
            ApplicationContextHolder.getBean("sysPermService");

    private static SysPermWhitelistService sysPermWhilelistService =
            ApplicationContextHolder.getBean("sysPermWhitelistService");

    private static Set<String> whitelistPermSet;

    static {
        List<SysPermWhitelist> sysPermWhitelistList = sysPermWhilelistService.getAllList();
        whitelistPermSet = sysPermWhitelistList.stream()
                .map(SysPermWhitelist::getPermUrl).collect(Collectors.toSet());
    }

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
        String token = request.getHeader(applicationConfig.getTokenHeaderKey());
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(applicationConfig.getTokenHeaderKey());
        }
        Claims c = JwtUtil.parseToken(token, applicationConfig.getTokenSigningKey());
        if (JwtUtil.isNullOrExpired(c)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            this.outputResponseMessage(response,
                    ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN, "用户会话已过期，请重新登录！"));
            return false;
        }
        String sessionId = (String) c.get("sessionId");
        Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.GLOBAL_CACHE.name());
        TokenData tokenData = cache.get(sessionId, TokenData.class);
        if (tokenData == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            this.outputResponseMessage(response,
                    ResponseResult.error(ErrorCodeEnum.UNAUTHORIZED_LOGIN, "用户会话已失效，请重新登录！"));
            return false;
        }
        TokenData.addToRequest(tokenData);
        // 如果url在权限资源白名单中，则不需要进行鉴权操作
        if (!tokenData.getIsAdmin() && !whitelistPermSet.contains(url)) {
            Set<String> urlSet = sysPermService.getCacheableSysPermSetByUserId(
                    tokenData.getSessionId(), tokenData.getUserId());
            if (!urlSet.contains(url)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                this.outputResponseMessage(response,
                        ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION));
                return false;
            }
        }
        if (JwtUtil.needToRefresh(c)) {
            String refreshedToken = JwtUtil.generateToken(c, applicationConfig.getTokenSigningKey());
            response.addHeader(applicationConfig.getRefreshedTokenHeaderKey(), refreshedToken);
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
        out.print(JSONObject.toJSONString(respObj));
        out.flush();
        out.close();
    }
}
