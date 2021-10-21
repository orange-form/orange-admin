package com.orange.demo.common.xxljob.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Job处理器的AOP，目前仅仅实现了将拦截后的异常记录到本地日志服务系统，记录后重新抛给xxl-job。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class JobHandlerAspect {

    @Pointcut("execution(public * com.orange.demo.*.handler..*(..))")
    public void handlerPointCut() {
        // 空注释，sonar要求的。
    }

    @Around("handlerPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Class<?> clazz = point.getTarget().getClass();
        try {
            return point.proceed();
        } catch (Exception e) {
            log.error("JobHandler [" + clazz.getSimpleName() + "] throws exception.", e);
            throw e;
        }
    }
}
