package com.orangeforms.uaaadmin.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户状态常量字典对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class UserStatus {

    /**
     * 正常。
     */
    public static final int NORMAL = 0;
    /**
     * 锁定。
     */
    public static final int LOCKED = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(NORMAL, "正常");
        DICT_MAP.put(LOCKED, "锁定");
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
    private UserStatus() {
    }
}
