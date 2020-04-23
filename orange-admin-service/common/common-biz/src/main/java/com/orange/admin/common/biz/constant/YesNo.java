package com.orange.admin.common.biz.constant;

import java.util.HashMap;
import java.util.Map;

public final class YesNo {

    /**
     * 是。
     */
    public static final int YES = 1;
    /**
     * 否。
     */
    public static final int NO = 0;

    public static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(YES, "是");
        DICT_MAP.put(NO, "否");
    }

    /**
     * 判断参数是否为当前常量字典的合法值。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(int value) {
        return DICT_MAP.containsKey(value);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private YesNo() {
    }
}