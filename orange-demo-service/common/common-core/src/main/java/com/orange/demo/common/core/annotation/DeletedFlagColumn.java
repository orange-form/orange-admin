package com.orange.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 主要用于标记逻辑删除字段。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeletedFlagColumn {

}
