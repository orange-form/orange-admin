package com.orange.demo.app.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 班级状态常量字典对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public final class ClassStatus {

    /**
     * 正常。
     */
    public static final int NORAML = 1;
    /**
     * 解散。
     */
    public static final int DELETED = -1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(NORAML, "正常");
        DICT_MAP.put(DELETED, "解散");
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
    private ClassStatus() {
    }
}
