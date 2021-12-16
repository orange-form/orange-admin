package com.orangeforms.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记Controller中的方法参数，参数解析器会根据该注解将请求中的JSON数据，映射到参数中的绑定字段。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyRequestBody {

    /**
     * 是否必须出现的参数。
     */
    boolean required() default false;
    /**
     * 解析时用到的JSON的key。
     */
    String value() default "";
    /**
     * 集合元素的ClassType。只有在接口参数为List<E>的时候，需要把E的class传入。
     * 缺省值Class.class表示没有设置。
     */
    Class<?> elementType() default Class.class;
}