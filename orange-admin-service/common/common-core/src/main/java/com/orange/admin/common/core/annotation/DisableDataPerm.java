package com.orange.admin.common.core.annotation;

import java.lang.annotation.*;

/**
 * 作为DisableDataPermAspect的切点。
 * 该注解仅能标记在方法上，方法内所有的查询语句，均不会被数据权限过滤。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableDataPerm {

}
