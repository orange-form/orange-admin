package com.orangeforms.application.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生状态常量字典对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class StudentStatus {

    /**
     * 正常。
     */
    public static final int NORMAL = 0;
    /**
     * 锁定。
     */
    public static final int LOCKED = 1;
    /**
     * 注销。
     */
    public static final int DELETED = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(NORMAL, "正常");
        DICT_MAP.put(LOCKED, "锁定");
        DICT_MAP.put(DELETED, "注销");
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
    private StudentStatus() {
    }
}
