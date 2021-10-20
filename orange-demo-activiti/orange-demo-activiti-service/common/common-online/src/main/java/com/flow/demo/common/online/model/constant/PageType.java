package com.flow.demo.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面类型常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class PageType {

    /**
     * 业务页面。
     */
    public static final int BIZ = 1;
    /**
     * 统计页面。
     */
    public static final int STATS = 5;
    /**
     * 流程页面。
     */
    public static final int FLOW = 10;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(BIZ, "业务页面");
        DICT_MAP.put(STATS, "统计页面");
        DICT_MAP.put(FLOW, "流程页面");
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
    private PageType() {
    }
}
