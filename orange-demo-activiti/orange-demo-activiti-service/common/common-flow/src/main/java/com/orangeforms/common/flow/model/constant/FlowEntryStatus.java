package com.orangeforms.common.flow.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流状态。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FlowEntryStatus {

    /**
     * 未发布。
     */
    public static final int UNPUBLISHED = 0;
    /**
     * 已发布。
     */
    public static final int PUBLISHED = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(UNPUBLISHED, "未发布");
        DICT_MAP.put(PUBLISHED, "已发布");
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
    private FlowEntryStatus() {
    }
}
