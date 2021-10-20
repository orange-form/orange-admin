package com.flow.demo.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 字段过滤类型常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FieldFilterType {

    /**
     * 无过滤。
     */
    public static final int NO_FILTER = 0;
    /**
     * 等于过滤。
     */
    public static final int EQUAL_FILTER = 1;
    /**
     * 范围过滤。
     */
    public static final int RANGE_FILTER = 2;
    /**
     * 模糊过滤。
     */
    public static final int LIKE_FILTER = 3;
    /**
     * IN LIST列表过滤。
     */
    public static final int IN_LIST_FILTER = 4;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(9);
    static {
        DICT_MAP.put(NO_FILTER, "无过滤");
        DICT_MAP.put(EQUAL_FILTER, "等于过滤");
        DICT_MAP.put(RANGE_FILTER, "范围过滤");
        DICT_MAP.put(LIKE_FILTER, "模糊过滤");
        DICT_MAP.put(IN_LIST_FILTER, "IN LIST列表过滤");
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
    private FieldFilterType() {
    }
}
