package com.orange.admin.upms.model.constant;

import java.util.HashMap;
import java.util.Map;

public final class SysUserType {

    /**
     * 管理员。
     */
    public static final int TYPE_ADMIN = 0;
    /**
     * 系统操作员。
     */
    public static final int TYPE_SYSTEM = 1;
    /**
     * 普通操作员。
     */
    public static final int TYPE_OPERATOR = 2;

    public static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(TYPE_ADMIN, "管理员");
        DICT_MAP.put(TYPE_SYSTEM, "系统操作员");
        DICT_MAP.put(TYPE_OPERATOR, "普通操作员");
    }

    /**
     * 判断参数是否为当前常量字典的合法值。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(int value) {
        return DICT_MAP.containsKey(value);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private SysUserType() {
    }
}
