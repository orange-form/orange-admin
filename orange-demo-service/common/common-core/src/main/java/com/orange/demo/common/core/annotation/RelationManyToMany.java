package com.orange.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 标注多对多的Model关系。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RelationManyToMany {

    /**
     * 多对多中间表的Mapper对象名称。
     *
     * @return 被关联的本地Service对象名称。
     */
    String relationMapperName();

    /**
     * 多对多关联表Model对象的Class对象。
     *
     * @return 被关联Model对象的Class对象。
     */
    Class<?> relationModelClass();

    /**
     * 多对多关联表Model对象中与主表关联的Id字段名称。
     *
     * @return 被关联Model对象的关联Id字段名称。
     */
    String relationMasterIdField();
}
