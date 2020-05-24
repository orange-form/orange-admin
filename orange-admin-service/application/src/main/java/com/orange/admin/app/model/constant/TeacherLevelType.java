package com.orange.admin.app.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 教师职级常量字典对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public final class TeacherLevelType {

    /**
     * 初级。
     */
    public static final int LOWER = 0;
    /**
     * 中级。
     */
    public static final int NORMAL = 1;
    /**
     * 高级。
     */
    public static final int HIGH = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(LOWER, "初级");
        DICT_MAP.put(NORMAL, "中级");
        DICT_MAP.put(HIGH, "高级");
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
    private TeacherLevelType() {
    }
}
