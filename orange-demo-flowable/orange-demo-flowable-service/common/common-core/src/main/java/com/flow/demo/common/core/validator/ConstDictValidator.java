package com.flow.demo.common.core.validator;

import cn.hutool.core.util.ReflectUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

/**
 * 数据字段自定义验证，用于验证Model中字符串字段的最大长度和最小长度。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class ConstDictValidator implements ConstraintValidator<ConstDictRef, Object> {

    private ConstDictRef constDictRef;

    @Override
    public void initialize(ConstDictRef constDictRef) {
        this.constDictRef = constDictRef;
    }

    @Override
    public boolean isValid(Object s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        Method method =
                ReflectUtil.getMethodByName(constDictRef.constDictClass(), "isValid");
        return ReflectUtil.invokeStatic(method, s);
    }
}
