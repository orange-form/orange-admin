package com.orangeforms.common.core.annotation;

import com.orangeforms.common.core.object.DummyClass;

import java.lang.annotation.*;

/**
 * 标识Model之间的字典关联关系。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RelationDict {

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
     * 被关联Model对象的关联Name字段名称。
     *
     * @return 被关联Model对象的关联Name字段名称。
     */
    String slaveNameField();

    /**
     * 被关联的本地Service对象名称。
     * 该参数的优先级高于 slaveService()，如果定义了该值，会优先使用加载service的bean对象。
     *
     * @return 被关联的本地Service对象名称。
     */
    String slaveServiceName() default "";

    /**
     * 被关联的本地Service对象CLass类型。
     *
     * @return 被关联的本地Service对象CLass类型。
     */
    Class<?> slaveServiceClass() default DummyClass.class;

    /**
     * 在同一个实体对象中，如果有一对一关联和字典关联，都是基于相同的主表字段，并关联到
     * 相同关联表的同一关联字段时，可以在字典关联的注解中引用被一对一注解标准的对象属性。
     * 从而在数据整合时，当前字典的数据可以直接取自"equalOneToOneRelationField"指定
     * 的字段，从而避免一次没必要的数据库查询操作，提升了加载显示的效率。
     *
     * @return 与该字典字段引用关系完全相同的一对一关联属性名称。
     */
    String equalOneToOneRelationField() default "";
}
