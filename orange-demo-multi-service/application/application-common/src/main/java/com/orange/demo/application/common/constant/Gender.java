package com.orange.demo.application.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 性别常量字典对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class Gender {

    /**
     * 男。
     */
    public static final int MALE = 1;
    /**
     * 女。
     */
    public static final int FEMALE = 0;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(MALE, "男");
        DICT_MAP.put(FEMALE, "女");
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
    private Gender() {
    }
}
