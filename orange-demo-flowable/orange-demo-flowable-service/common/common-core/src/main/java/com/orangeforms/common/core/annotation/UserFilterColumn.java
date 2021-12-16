package com.orangeforms.common.core.annotation;

import java.lang.annotation.*;

/**
 * 主要用于标记数据权限中基于UserId进行过滤的字段。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserFilterColumn {

}
