package com.orangeforms.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 表单类别常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FormKind {

    /**
     * 弹框。
     */
    public static final int DIALOG = 1;
    /**
     * 跳页。
     */
    public static final int NEW_PAGE = 5;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(DIALOG, "弹框列表");
        DICT_MAP.put(NEW_PAGE, "跳页类别");
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
    private FormKind() {
    }
}
