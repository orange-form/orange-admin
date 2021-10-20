package com.flow.demo.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证规则类型常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class RuleType {

    /**
     * 只允许整数。
     */
    public static final int INTEGER_ONLY = 1;
    /**
     * 只允许数字。
     */
    public static final int DIGITAL_ONLY = 2;
    /**
     * 只允许英文字符。
     */
    public static final int LETTER_ONLY = 3;
    /**
     * 范围验证。
     */
    public static final int RANGE = 4;
    /**
     * 邮箱格式验证。
     */
    public static final int EMAIL = 5;
    /**
     * 手机格式验证。
     */
    public static final int MOBILE = 6;
    /**
     * 自定义验证。
     */
    public static final int CUSTOM = 100;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(7);
    static {
        DICT_MAP.put(INTEGER_ONLY, "只允许整数");
        DICT_MAP.put(DIGITAL_ONLY, "只允许数字");
        DICT_MAP.put(LETTER_ONLY, "只允许英文字符");
        DICT_MAP.put(RANGE, "范围验证");
        DICT_MAP.put(EMAIL, "邮箱格式验证");
        DICT_MAP.put(MOBILE, "手机格式验证");
        DICT_MAP.put(CUSTOM, "自定义验证");
    }

    /**
     * 判断参数是否为当前常量字典的合法值。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(Integer value) {
        return value != null && DICT_MAP.containsKey(value);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private RuleType() {
    }
}
