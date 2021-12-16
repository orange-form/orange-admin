package com.orangeforms.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 在线表单虚拟字段类型常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class VirtualType {

    /**
     * 聚合。
     */
    public static final int AGGREGATION = 0;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(AGGREGATION, "聚合");
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
    private VirtualType() {
    }
}
