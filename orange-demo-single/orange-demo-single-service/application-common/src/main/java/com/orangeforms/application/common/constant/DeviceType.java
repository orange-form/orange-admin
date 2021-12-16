package com.orangeforms.application.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备类型常量字典对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public final class DeviceType {

    /**
     * iOS。
     */
    public static final int IOS = 0;
    /**
     * Android。
     */
    public static final int ANDROID = 1;
    /**
     * PC。
     */
    public static final int PC = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(IOS, "iOS");
        DICT_MAP.put(ANDROID, "Android");
        DICT_MAP.put(PC, "PC");
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
    private DeviceType() {
    }
}
