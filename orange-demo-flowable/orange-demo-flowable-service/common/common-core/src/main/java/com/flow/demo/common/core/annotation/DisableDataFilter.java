package com.flow.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 作为DisableDataFilterAspect的切点。
 * 该注解仅能标记在方法上，方法内所有的查询语句，均不会被Mybatis拦截器过滤数据。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableDataFilter {

}
