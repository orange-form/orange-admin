package com.orangeforms.application.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 学科常量字典对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class Subject {

    /**
     * 语文。
     */
    public static final int CHINESE = 0;
    /**
     * 数学。
     */
    public static final int MATCH = 1;
    /**
     * 英语。
     */
    public static final int ENGLISH = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(CHINESE, "语文");
        DICT_MAP.put(MATCH, "数学");
        DICT_MAP.put(ENGLISH, "英语");
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
    private Subject() {
    }
}
