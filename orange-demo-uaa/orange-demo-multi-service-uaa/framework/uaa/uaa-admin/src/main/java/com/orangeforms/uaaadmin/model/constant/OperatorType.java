package com.orangeforms.uaaadmin.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户类型常量字典对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class OperatorType {

    /**
     * 管理员。
     */
    public static final int ADMIN = 0;
    /**
     * 普通操作员。
     */
    public static final int NORMAL = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(ADMIN, "管理员");
        DICT_MAP.put(NORMAL, "普通操作员");
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
    private OperatorType() {
    }
}
