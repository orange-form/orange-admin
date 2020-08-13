package com.orange.demo.upmsinterface.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限字类型常量对象。
 *
 * @author Orange Team
 * @date 2020-08-08
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
        DICT_MAP.put(0, "表单权限字");
        DICT_MAP.put(1, "表单片段布局权限字");
        DICT_MAP.put(2, "操作权限字");
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
