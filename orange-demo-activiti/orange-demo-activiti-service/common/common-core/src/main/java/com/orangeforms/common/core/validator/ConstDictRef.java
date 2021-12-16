package com.orangeforms.common.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义在Model对象中，标注字段值引用自指定的常量字典，和ConstDictRefValidator对象配合完成数据验证。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConstDictValidator.class)
public @interface ConstDictRef {

    /**
     * 引用的常量字典对象，该对象必须包含isValid的静态方法。
     *
     * @return 最大长度。
     */
    Class<?> constDictClass();

    /**
     * 超过边界后的错误消息提示。
     *
     * @return 错误提示。
     */
    String message() default "无效的字典引用值！";

    /**
     * 验证分组。
     *
     * @return 验证分组。
     */
    Class<?>[] groups() default {};

    /**
     * 载荷对象类型。
     *
     * @return 载荷对象。
     */
    Class<? extends Payload>[] payload() default {};
}
