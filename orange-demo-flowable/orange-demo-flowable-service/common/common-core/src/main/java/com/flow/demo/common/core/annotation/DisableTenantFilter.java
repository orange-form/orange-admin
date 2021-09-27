package com.flow.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 仅用于微服务的多租户项目。
 * 用于注解DAO层Mapper对象的租户过滤规则。被包含的方法将不会进行租户Id的过滤。
 * 对于tk mapper和mybatis plus中的内置方法，可以直接指定方法名即可，如：selectOne。
 * 需要说明的是，在大多数场景下，只要在实体对象中指定了租户Id字段，基于该主表的绝大部分增删改操作，
 * 都需要经过租户Id过滤，仅当查询非常复杂，或者主表不在SQL语句之中的时候，可以通过该注解禁用该SQL，
 * 并根据需求通过手动的方式实现租户过滤。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableTenantFilter {

    /**
     * 包含的方法名称数组。该值不能为空，因为如想取消所有方法的租户过滤，
     * 可以通过在实体对象中不指定租户Id字段注解的方式实现。
     *
     * @return 被包括的方法名称数组。
     */
    String[] includeMethodName();
}
