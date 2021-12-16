package com.orangeforms.common.core.aop;

import com.orangeforms.common.core.annotation.MyDataSource;
import com.orangeforms.common.core.config.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 多数据源AOP切面处理类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DataSourceAspect {

    /**
     * 所有配置MyDataSource注解的Service实现类。
     */
    @Pointcut("execution(public * com.orangeforms..service..*(..)) " +
            "&& @target(com.orangeforms.common.core.annotation.MyDataSource)")
    public void datasourcePointCut() {
        // 空注释，避免sonar警告
    }

    @Around("datasourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Class<?> clazz = point.getTarget().getClass();
        MyDataSource ds = clazz.getAnnotation(MyDataSource.class);
        // 通过判断 DataSource 中的值来判断当前方法应用哪个数据源
        Integer originalType = DataSourceContextHolder.setDataSourceType(ds.value());
        log.debug("set datasource is " + ds.value());
        try {
            return point.proceed();
        } finally {
            DataSourceContextHolder.unset(originalType);
            log.debug("unset datasource is " + originalType);
        }
    }
}
