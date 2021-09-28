package com.orange.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 主要用于标记更新字段。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobUpdateTimeColumn {

}
