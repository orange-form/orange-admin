package com.flow.demo.common.datafilter.interceptor;

import com.flow.demo.common.core.object.GlobalThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 主要用于初始化，通过Mybatis拦截器插件进行数据过滤的标记。
 * 在调用controller接口处理方法之前，必须强制将数据过滤标记设置为缺省值。
 * 这样可以避免使用当前线程在处理上一个请求时，未能正常清理的数据过滤标记值。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class DataFilterInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 每次进入Controller接口之前，均主动打开数据权限验证。
        // 可以避免该Servlet线程在处理之前的请求时异常退出，从而导致该状态数据没有被正常清除。
        GlobalThreadLocal.setDataFilter(true);
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
        GlobalThreadLocal.clearDataFilter();
    }
}
