package com.flow.demo.common.flow.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流绑定表单类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FlowBindFormType {

    /**
     * 在线表单。
     */
    public static final int ONLINE_FORM = 0;
    /**
     * 路由表单。
     */
    public static final int ROUTER_FORM = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(ONLINE_FORM, "在线表单");
        DICT_MAP.put(ROUTER_FORM, "路由表单");
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
    private FlowBindFormType() {
    }
}
