package com.orangeforms.common.flow.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流任务类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FlowTaskType {

    /**
     * 其他类型任务。
     */
    public static final int OTHER_TYPE = 0;
    /**
     * 用户任务。
     */
    public static final int USER_TYPE = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(OTHER_TYPE, "其他任务类型");
        DICT_MAP.put(USER_TYPE, "用户任务类型");
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
    private FlowTaskType() {
    }
}
