package com.orange.demo.upms.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单类型常量对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public final class SysMenuType {

    /**
     * 目录菜单。
     */
    public static final int TYPE_DIRECTORY = 0;
    /**
     * 普通菜单。
     */
    public static final int TYPE_MENU = 1;
    /**
     * 表单片段类型。
     */
    public static final int TYPE_UI_FRAGMENT = 2;
    /**
     * 按钮类型。
     */
    public static final int TYPE_BUTTON = 3;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(4);
    static {
        DICT_MAP.put(0, "目录菜单");
        DICT_MAP.put(1, "普通菜单");
        DICT_MAP.put(2, "表单片段类型");
        DICT_MAP.put(3, "按钮类型");
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
    private SysMenuType() {
    }
}
