package com.orangeforms.common.core.annotation;

import java.lang.annotation.*;

/**
 * 作为DisableDataFilterAspect的切点。
 * 该注解仅能标记在方法上，方法内所有的查询语句，均不会被Mybatis插件进行数据过滤。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableDataFilter {

}
