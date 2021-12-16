package com.orangeforms.common.flow.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程变量类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FlowVariableType {

    /**
     * 流程实例变量。
     */
    public static final int INSTANCE = 0;
    /**
     * 任务变量。
     */
    public static final int TASK = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(INSTANCE, "流程实例变量");
        DICT_MAP.put(TASK, "任务变量");
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
    private FlowVariableType() {
    }
}
