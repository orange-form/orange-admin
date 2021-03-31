package com.orange.demo.courseclassapi.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 课程难度常量字典对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class CourseDifficult {

    /**
     * 容易。
     */
    public static final int NORMAL = 0;
    /**
     * 普通。
     */
    public static final int MIDDLE = 1;
    /**
     * 困难。
     */
    public static final int HIGH = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(NORMAL, "容易");
        DICT_MAP.put(MIDDLE, "普通");
        DICT_MAP.put(HIGH, "困难");
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
    private CourseDifficult() {
    }
}
