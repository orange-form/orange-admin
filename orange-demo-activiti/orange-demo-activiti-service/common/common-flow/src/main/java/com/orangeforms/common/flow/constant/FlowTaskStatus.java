package com.orangeforms.common.flow.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流任务类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FlowTaskStatus {

    /**
     * 已提交。
     */
    public static final int SUBMITTED = 0;
    /**
     * 审批中。
     */
    public static final int APPROVING = 1;
    /**
     * 被拒绝。
     */
    public static final int REFUSED = 2;
    /**
     * 已结束。
     */
    public static final int FINISHED = 3;
    /**
     * 提前停止。
     */
    public static final Integer STOPPED = 4;
    /**
     * 已取消。
     */
    public static final Integer CANCELLED = 5;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(SUBMITTED, "已提交");
        DICT_MAP.put(APPROVING, "审批中");
        DICT_MAP.put(REFUSED, "被拒绝");
        DICT_MAP.put(FINISHED, "已结束");
        DICT_MAP.put(STOPPED, "提前停止");
        DICT_MAP.put(CANCELLED, "已取消");
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
    private FlowTaskStatus() {
    }
}
