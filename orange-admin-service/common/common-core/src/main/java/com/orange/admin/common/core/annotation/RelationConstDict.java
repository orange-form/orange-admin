package com.orange.admin.common.core.annotation;

import java.lang.annotation.*;

/**
 * 标识Model和常量字典之间的关联关系。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RelationConstDict {

    /**
     * 当前对象的关联Id字段名称。
     *
     * @return 当前对象的关联Id字段名称。
     */
    String masterIdField();

    /**
     * 被关联的常量字典的Class对象。
     *
     * @return 关联的常量字典的Class对象。
     */
    Class<?> constantDictClass();
}
