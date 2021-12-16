package com.orangeforms.common.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义在Model或Dto对象中，UTF-8编码的字符串字段长度的上限和下限，和TextLengthValidator对象配合完成数据验证。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TextLengthValidator.class)
public @interface TextLength {

    /**
     * 字符串字段的最小长度。
     *
     * @return 最小长度。
     */
    int min() default 0;

    /**
     * 字符串字段的最大长度。
     *
     * @return 最大长度。
     */
    int max() default Integer.MAX_VALUE;

    /**
     * 超过边界后的错误消息提示。
     *
     * @return 错误提示。
     */
    String message() default "字段长度超过最大字节数！";

    /**
     * 验证分组。
     *
     * @return 验证分组。
     */
    Class<?>[] groups() default { };

    /**
     * 载荷对象类型。
     *
     * @return 载荷对象。
     */
    Class<? extends Payload>[] payload() default { };
}
