package com.flow.demo.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 关联类型常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class RelationType {

    /**
     * 一对一关联。
     */
    public static final int ONE_TO_ONE = 0;
    /**
     * 一对多关联。
     */
    public static final int ONE_TO_MANY = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(ONE_TO_ONE, "一对一关联");
        DICT_MAP.put(ONE_TO_MANY, "一对多关联");
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
    private RelationType() {
    }
}
