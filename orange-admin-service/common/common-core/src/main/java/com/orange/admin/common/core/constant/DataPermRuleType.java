package com.orange.admin.common.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限规则类型常量类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public final class DataPermRuleType {

    /**
     * 查看全部。
     */
    public static final int TYPE_ALL = 0;

    /**
     * 仅查看当前用户
     */
    public static final int TYPE_USER_ONLY = 1;

    /**
     * 仅查看当前部门
     */
    public static final int TYPE_DEPT_ONLY = 2;

    /**
     * 自定义部门列表
     */
    public static final int TYPE_CUSTOM_DETP_LIST = 5;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(4);
    static {
        DICT_MAP.put(0, "查看全部");
        DICT_MAP.put(1, "仅查看当前用户");
        DICT_MAP.put(2, "仅查看所在部门");
        DICT_MAP.put(5, "自定义部门列表");
    }

    /**
     * 判断参数是否为当前常量字典的合法取值范围。
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
    private DataPermRuleType() {
    }
}
