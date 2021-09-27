package com.flow.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 用于注解DAO层Mapper对象的数据权限规则。
 * 由于框架使用了tk.mapper，所以并非所有的Mapper接口均在当前Mapper对象中定义，有一部分被tk.mapper封装，如selectAll等。
 * 如果需要排除tk.mapper中的方法，可以直接使用tk.mapper基类所声明的方法名称即可。
 * 另外，比较特殊的场景是，因为tk.mapper是通用框架，所以同样的selectAll方法，可以获取不同的数据集合，因此在service中如果
 * 出现两个不同的方法调用Mapper的selectAll方法，但是一个需要参与过滤，另外一个不需要参与，那么就需要修改当前类的Mapper方法，
 * 将其中一个方法重新定义一个具体的接口方法，并重新设定其是否参与数据过滤。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableDataPerm {

    /**
     * 排除的方法名称数组。如果为空，所有的方法均会被Mybaits拦截注入权限过滤条件。
     *
     * @return 被排序的方法名称数据。
     */
    String[] excluseMethodName() default {};
}
