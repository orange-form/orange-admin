package com.flow.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 标注多对多的Model关系。
 * 重要提示：由于多对多关联表数据，很多时候都不需要跟随主表数据返回，所以该注解不会在
 * 生成的时候自动添加到实体类字段上，需要的时候，用户可自行手动添加。
 *
 * @author Jerry
 * @date 2021-06-06
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
