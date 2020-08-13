package com.orange.demo.common.core.annotation;

import com.orange.demo.common.core.object.DummyClass;

import java.lang.annotation.*;

/**
 * 标识Model之间的一对一关联关系。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RelationOneToOne {

    /**
     * 当前对象的关联Id字段名称。
     *
     * @return 当前对象的关联Id字段名称。
     */
    String masterIdField();

    /**
     * 被关联Model对象的Class对象。
     *
     * @return 被关联Model对象的Class对象。
     */
    Class<?> slaveModelClass();

    /**
     * 被关联Model对象的关联Id字段名称。
     *
     * @return 被关联Model对象的关联Id字段名称。
     */
    String slaveIdField();

    /**
     * 被关联远程调用对象的Class对象。
     *
     * @return 被关联远程调用对象的Class对象。
     */
    Class<?> slaveClientClass() default DummyClass.class;

    /**
     * 被关联的本地Service对象名称。
     *
     * @return 被关联的本地Service对象名称。
     */
    String slaveServiceName() default "";

    /**
     * 在一对一关联时，是否加载从表的字典关联。
     *
     * @return 是否加载从表的字典关联。true关联，false则只返回从表自身数据。
     */
    boolean loadSlaveDict() default true;
}
