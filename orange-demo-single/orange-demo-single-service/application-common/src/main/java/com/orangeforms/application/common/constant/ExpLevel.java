package com.orangeforms.application.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 经验等级常量字典对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public final class ExpLevel {

    /**
     * 初级学员。
     */
    public static final int LOWER = 0;
    /**
     * 中级学员。
     */
    public static final int MIDDLE = 1;
    /**
     * 高级学员。
     */
    public static final int HIGH = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(LOWER, "初级学员");
        DICT_MAP.put(MIDDLE, "中级学员");
        DICT_MAP.put(HIGH, "高级学员");
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
    private ExpLevel() {
    }
}
