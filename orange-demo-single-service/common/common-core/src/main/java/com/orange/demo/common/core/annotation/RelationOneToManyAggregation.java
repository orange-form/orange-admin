package com.orange.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 主要用于一对多的Model关系。标注通过从表关联字段计算主表聚合计算字段的规则。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RelationOneToManyAggregation {

    /**
     * 当前对象的关联Id字段名称。
     *
     * @return 当前对象的关联Id字段名称。
     */
    String masterIdField();

    /**
     * 被关联的本地Service对象名称。
     *
     * @return 被关联的本地Service对象名称。
     */
    String slaveServiceName();

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
     * 被关联Model对象中参与计算的聚合类型。具体数值参考AggregationType对象。
     *
     * @return 被关联Model对象中参与计算的聚合类型。
     */
    int aggregationType();

    /**
     * 被关联Model对象中参与聚合计算的字段名称。
     *
     * @return 被关联Model对象中参与计算字段的名称。
     */
    String aggregationField();
}
