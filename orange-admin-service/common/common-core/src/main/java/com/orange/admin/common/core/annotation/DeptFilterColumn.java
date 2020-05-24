package com.orange.admin.common.core.annotation;

import java.lang.annotation.*;

/**
 * 主要用于标记数据权限中基于DeptId进行过滤的字段。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeptFilterColumn {

}
