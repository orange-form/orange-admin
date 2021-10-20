package com.flow.demo.webadmin.upms.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限字类型常量对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class SysPermCodeType {

    /**
     * 表单权限字。
     */
    public static final int TYPE_FORM = 0;
    /**
     * 表单片段布局权限字。
     */
    public static final int TYPE_FRAGMENT = 1;
    /**
     * 操作权限字。
     */
    public static final int TYPE_OPERATION = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(TYPE_FORM, "表单权限字");
        DICT_MAP.put(TYPE_FRAGMENT, "表单片段布局权限字");
        DICT_MAP.put(TYPE_OPERATION, "操作权限字");
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
    private SysPermCodeType() {
    }
}
