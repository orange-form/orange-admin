package com.flow.demo.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面状态常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class PageStatus {

    /**
     * 编辑基础信息。
     */
    public static final int BASIC = 0;
    /**
     * 编辑数据模型。
     */
    public static final int DATASOURCE = 1;
    /**
     * 设计表单。
     */
    public static final int FORM_DESIGN = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(4);
    static {
        DICT_MAP.put(BASIC, "编辑基础信息");
        DICT_MAP.put(DATASOURCE, "编辑数据模型");
        DICT_MAP.put(FORM_DESIGN, "设计表单");
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
    private PageStatus() {
    }
}
