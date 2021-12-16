package com.orangeforms.common.core.aop;

import com.orangeforms.common.core.annotation.MyDataSourceResolver;
import com.orangeforms.common.core.util.DataSourceResolver;
import com.orangeforms.common.core.config.DataSourceContextHolder;
import com.orangeforms.common.core.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于自定义解析规则的多数据源AOP切面处理类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DataSourceResolveAspect {

    private final Map<Class<? extends DataSourceResolver>, DataSourceResolver> resolverMap = new HashMap<>();

    /**
     * 所有配置 MyDataSource 注解的Service。
     */
    @Pointcut("execution(public * com.orangeforms..service..*(..)) " +
            "&& @target(com.orangeforms.common.core.annotation.MyDataSourceResolver)")
    public void datasourceResolverPointCut() {
        // 空注释，避免sonar警告
    }

    @Around("datasourceResolverPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Class<?> clazz = point.getTarget().getClass();
        MyDataSourceResolver dsr = clazz.getAnnotation(MyDataSourceResolver.class);
        Class<? extends DataSourceResolver> resolverClass = dsr.resolver();
        DataSourceResolver resolver = resolverMap.get(resolverClass);
        if (resolver == null) {
            resolver = ApplicationContextHolder.getBean(resolverClass);
            resolverMap.put(resolverClass, resolver);
        }
        int type = resolver.resolve(dsr.arg(), point.getArgs());
        // 通过判断 DataSource 中的值来判断当前方法应用哪个数据源
        Integer originalType = DataSourceContextHolder.setDataSourceType(type);
        log.debug("set datasource is " + type);
        try {
            return point.proceed();
        } finally {
            DataSourceContextHolder.unset(originalType);
            log.debug("unset datasource is " + originalType);
        }
    }
}
