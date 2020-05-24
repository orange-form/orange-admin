package com.orange.admin.common.core.annotation;

import java.lang.annotation.*;

/**
 * 主要用于标记无需Token验证的接口
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuthInterface {
}
