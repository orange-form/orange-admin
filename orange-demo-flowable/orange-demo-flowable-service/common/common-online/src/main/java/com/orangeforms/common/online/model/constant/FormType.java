package com.orangeforms.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 表单类型常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FormType {

    /**
     * 查询表单。
     */
    public static final int QUERY = 1;
    /**
     * 编辑表单。
     */
    public static final int FORM = 5;
    /**
     * 流程表单。
     */
    public static final int FLOW = 10;
    /**
     * 流程工单表单。
     */
    public static final int FLOW_WORK_ORDER = 11;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(QUERY, "查询表单");
        DICT_MAP.put(FORM, "编辑表单");
        DICT_MAP.put(FLOW, "流程表单");
        DICT_MAP.put(FLOW_WORK_ORDER, "流程工单表单");
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
    private FormType() {
    }
}
