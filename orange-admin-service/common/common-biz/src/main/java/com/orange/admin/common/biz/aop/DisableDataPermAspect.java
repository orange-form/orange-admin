package com.orange.admin.common.biz.aop;

import com.orange.admin.common.core.object.GlobalThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 禁用数据权限过滤的AOP处理类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DisableDataPermAspect {

    /**
     * 所有标记了DisableDataPerm注解的方法。
     */
    @Pointcut("@annotation(com.orange.admin.common.core.annotation.DisableDataPerm)")
    public void disableDataPermPointCut() {
        // 空注释，避免sonar警告
    }

    @Around("disableDataPermPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        boolean dataPermEnabled = GlobalThreadLocal.setDataPerm(false);
        try {
            return point.proceed();
        } finally {
            GlobalThreadLocal.setDataPerm(dataPermEnabled);
        }
    }
}
