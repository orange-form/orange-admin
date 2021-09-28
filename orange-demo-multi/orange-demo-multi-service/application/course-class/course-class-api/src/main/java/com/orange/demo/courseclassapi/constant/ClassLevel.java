package com.orange.demo.courseclassapi.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 班级级别常量字典对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class ClassLevel {

    /**
     * 初级班。
     */
    public static final int NORMAL = 0;
    /**
     * 中级班。
     */
    public static final int MIDDLE = 1;
    /**
     * 高级班。
     */
    public static final int HIGH = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(NORMAL, "初级班");
        DICT_MAP.put(MIDDLE, "中级班");
        DICT_MAP.put(HIGH, "高级班");
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
    private ClassLevel() {
    }
}
