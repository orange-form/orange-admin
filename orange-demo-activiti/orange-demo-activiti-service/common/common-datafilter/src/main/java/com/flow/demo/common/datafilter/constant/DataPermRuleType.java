package com.flow.demo.common.datafilter.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限规则类型常量类。
 *
 * @author Jerry
 * @date 2021-06-06
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
     * 所在部门及子部门
     */
    public static final int TYPE_DEPT_AND_CHILD_DEPT = 3;

    /**
     * 多部门及子部门
     */
    public static final int TYPE_MULTI_DEPT_AND_CHILD_DEPT = 4;

    /**
     * 自定义部门列表
     */
    public static final int TYPE_CUSTOM_DEPT_LIST = 5;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(6);
    static {
        DICT_MAP.put(0, "查看全部");
        DICT_MAP.put(1, "仅查看当前用户");
        DICT_MAP.put(2, "仅查看所在部门");
        DICT_MAP.put(3, "所在部门及子部门");
        DICT_MAP.put(4, "多部门及子部门");
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
