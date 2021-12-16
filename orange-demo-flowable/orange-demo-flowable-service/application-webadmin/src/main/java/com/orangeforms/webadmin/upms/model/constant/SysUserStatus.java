package com.orangeforms.webadmin.upms.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户状态常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class SysUserStatus {

    /**
     * 正常状态。
     */
    public static final int STATUS_NORMAL = 0;
    /**
     * 锁定状态。
     */
    public static final int STATUS_LOCKED = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(STATUS_NORMAL, "正常状态");
        DICT_MAP.put(STATUS_LOCKED, "锁定状态");
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
    private SysUserStatus() {
    }
}
