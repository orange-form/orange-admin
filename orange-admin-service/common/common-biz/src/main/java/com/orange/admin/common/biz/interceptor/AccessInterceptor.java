package com.orange.admin.common.biz.interceptor;

import com.orange.admin.common.core.object.GlobalThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 服务访问日志的拦截器，主要完成记录接口调用时长。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Slf4j
public class AccessInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> STARTTIME_THREADLOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        STARTTIME_THREADLOCAL.set(System.currentTimeMillis());
        // 每次进入Controller接口之前，均主动打开数据权限验证。
        // 可以避免该Servlet线程在处理之前的请求时异常退出，从而导致该状态数据没有被正常清除。
        GlobalThreadLocal.setDataPerm(true);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 这里需要加注释，否则sonar不happy。
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        long startTime = STARTTIME_THREADLOCAL.get();
        long elapse = System.currentTimeMillis() - startTime;
        String urlPath = request.getRequestURI();
        String controllerMethod = "Unknown";
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            String controllerName = hm.getBean().getClass().getName();
            // 这里将cglib织入的部分去掉，提升日志的可读性
            int index = controllerName.indexOf("$$");
            if (index > 0) {
                controllerName = controllerName.substring(0, index);
            }
            controllerMethod = controllerName + "." + hm.getMethod().getName();
        }
        log.info("access: {} -- elapse {} ms -- {}.", controllerMethod, elapse, urlPath);
        STARTTIME_THREADLOCAL.remove();
        GlobalThreadLocal.clearDataPerm();
    }
}
