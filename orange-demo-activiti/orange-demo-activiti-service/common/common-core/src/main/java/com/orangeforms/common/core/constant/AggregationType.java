package com.orangeforms.common.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 聚合计算的常量类型对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class AggregationType {

    /**
     * sum 计数
     */
    public static final int SUM = 0;
    /**
     * count 汇总
     */
    public static final int COUNT = 1;
    /**
     * average 平均值
     */
    public static final int AVG = 2;
    /**
     * min 最小值
     */
    public static final int MIN = 3;
    /**
     * max 最大值
     */
    public static final int MAX = 4;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(5);
    static {
        DICT_MAP.put(SUM, "累计总和");
        DICT_MAP.put(COUNT, "数量总和");
        DICT_MAP.put(AVG, "平均值");
        DICT_MAP.put(MIN, "最小值");
        DICT_MAP.put(MAX, "最大值");
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
     * 获取与SQL对应的聚合函数字符串名称。
     *
     * @return 聚合函数名称。
     */
    public static String getAggregationFunction(Integer aggregationType) {
        switch (aggregationType) {
            case COUNT:
                return "COUNT";
            case AVG:
                return "AVG";
            case SUM:
                return "SUM";
            case MAX:
                return "MAX";
            case MIN:
                return "MIN";
            default:
                throw new IllegalArgumentException("无效的聚合类型！");
        }
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private AggregationType() {
    }
}
