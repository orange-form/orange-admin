package com.orangeforms.common.core.aop;

import com.orangeforms.common.core.base.service.BaseDictService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 字典缓存同步的AOP。该AOP的优先级必须比事务切面的优先级高，因此会在事务外执行该切面的代码。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Slf4j
public class DictCacheSyncAspect {

    /**
     * BaseDictService 字典服务父类中的字典数据增删改的方法。
     */
    @Pointcut("execution(public * com.orangeforms..BaseDictService.saveNew (..)) " +
            "|| execution(public * com.orangeforms..BaseDictService.update (..)) " +
            "|| execution(public * com.orangeforms..BaseDictService.remove (..))" )
    public void baseDictServicePointCut() {
        // 空注释，避免sonar警告
    }

    @SuppressWarnings("unchecked")
    @Around("baseDictServicePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object arg = joinPoint.getArgs()[0];
        if ("saveNew".equals(methodName)) {
            Object data = joinPoint.proceed();
            BaseDictService<Object, Serializable> service =
                    (BaseDictService<Object, Serializable>) joinPoint.getTarget();
            // 这里参数必须使用saveNew方法的返回对象，因为里面包含实际主键值。
            service.putDictionaryCache(data);
            return data;
        } else if ("update".equals(methodName)) {
            Object data = joinPoint.proceed();
            BaseDictService<Object, Serializable> service =
                    (BaseDictService<Object, Serializable>) joinPoint.getTarget();
            // update的方法返回的是boolean，因此这里的参数需要使用第一个参数即可。
            service.putDictionaryCache(arg);
            return data;
        } else {
            // remove
            BaseDictService<Object, Serializable> service =
                    (BaseDictService<Object, Serializable>) joinPoint.getTarget();
            service.removeDictionaryCache((Serializable) arg);
            return joinPoint.proceed();
        }
    }
}
