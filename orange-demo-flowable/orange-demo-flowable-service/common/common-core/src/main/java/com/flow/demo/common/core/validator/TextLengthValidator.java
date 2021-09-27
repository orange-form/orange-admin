package com.flow.demo.common.core.validator;

import org.apache.commons.lang3.CharUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 数据字段自定义验证，用于验证Model中UTF-8编码的字符串字段的最大长度和最小长度。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class TextLengthValidator implements ConstraintValidator<TextLength, String> {

    private TextLength textLength;

    @Override
    public void initialize(TextLength textLength) {
        this.textLength = textLength;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (CharUtils.isAscii(c)) {
                ++length;
            } else {
                length += 2;
            }
        }
        return length >= textLength.min() && length <= textLength.max();
    }
}
