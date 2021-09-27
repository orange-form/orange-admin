package com.flow.demo.common.core.annotation;

import java.lang.annotation.*;

/**
 * 主要用于标记更新字段。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobUpdateTimeColumn {

}
