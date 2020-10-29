package com.orange.demo.common.core.aop;

import com.alibaba.fastjson.JSON;
import com.orange.demo.common.core.constant.ApplicationConstant;
import com.orange.demo.common.core.util.MyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录接口的链路traceId、请求参数、应答数据、错误信息和调用时长。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class AccessLogAspect {

    /**
     * 所有controller方法。
     */
    @Pointcut("execution(public * com.orange.demo..controller..*(..))")
    public void controllerPointCut() {
        // 空注释，避免sonar警告
    }

    @Around("controllerPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // 请求流水号
        String traceId = MyCommonUtil.generateUuid();
        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        response.setHeader(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        MDC.put(ApplicationConstant.HTTP_HEADER_TRACE_ID, traceId);
        long start = System.currentTimeMillis();
        // 获取方法参数
        List<Object> httpReqArgs = new ArrayList<>();
        Object[] args = joinPoint.getArgs();
        for (Object object : args) {
            if (!(object instanceof HttpServletRequest)
                    && !(object instanceof HttpServletResponse)
                    && !(object instanceof MultipartFile)) {
                httpReqArgs.add(object);
            }
        }
        String url = request.getRequestURI();
		String params = JSON.toJSONString(httpReqArgs);
        log.info("开始请求，traceId={}, url={}, reqData={}", traceId, url, params);
		Object result = null;
        try {
            // 调用原来的方法
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("请求报错，traceId={}, url={}, reqData={}, error={}", traceId, url, params, e.getMessage());
            throw e;
        } finally {
            // 获取应答报文及接口处理耗时
            String respData = result == null ? null : JSON.toJSONString(result);
            log.info("请求完成, traceId={}, url={}，elapse={}ms, respData={}",
                    traceId, url, (System.currentTimeMillis() - start), respData);
        }
        return result;
    }
}