package com.flow.demo.common.flow.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流任务触发BUTTON。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FlowApprovalType {

    /**
     * 保存。
     */
    public static final String SAVE = "save";
    /**
     * 同意。
     */
    public static final String AGREE = "agree";
    /**
     * 拒绝。
     */
    public static final String REFUSE = "refuse";
    /**
     * 指派。
     */
    public static final String TRANSFER = "transfer";
    /**
     * 多实例会签。
     */
    public static final String MULTI_SIGN = "multi_sign";
    /**
     * 会签同意。
     */
    public static final String MULTI_AGREE = "multi_agree";
    /**
     * 会签拒绝。
     */
    public static final String MULTI_REFUSE = "multi_refuse";
    /**
     * 会签弃权。
     */
    public static final String MULTI_ABSTAIN = "multi_abstain";
    /**
     * 多实例加签。
     */
    public static final String MULTI_CONSIGN = "multi_consign";
    /**
     * 中止。
     */
    public static final String STOP = "stop";

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(SAVE, "保存");
        DICT_MAP.put(AGREE, "同意");
        DICT_MAP.put(REFUSE, "拒绝");
        DICT_MAP.put(TRANSFER, "指派");
        DICT_MAP.put(MULTI_SIGN, "多实例会签");
        DICT_MAP.put(MULTI_AGREE, "会签同意");
        DICT_MAP.put(MULTI_REFUSE, "会签拒绝");
        DICT_MAP.put(MULTI_ABSTAIN, "会签弃权");
        DICT_MAP.put(MULTI_CONSIGN, "多实例加签");
        DICT_MAP.put(STOP, "中止");
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
    private FlowApprovalType() {
    }
}
