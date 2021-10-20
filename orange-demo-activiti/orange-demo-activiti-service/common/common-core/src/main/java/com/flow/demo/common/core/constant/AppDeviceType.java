package com.flow.demo.common.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * App 登录的设备类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class AppDeviceType {

    /**
     * 移动端 (如果不考虑区分android或ios的，可以使用该值)
     */
    public static final int MOBILE = 0;
    /**
     * android
     */
    public static final int ANDROID = 1;
    /**
     * iOS
     */
    public static final int IOS = 2;
    /**
     * 微信公众号和小程序
     */
    public static final int WEIXIN = 3;
    /**
     * PC WEB
     */
    public static final int WEB = 4;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(5);
    static {
        DICT_MAP.put(MOBILE, "移动端");
        DICT_MAP.put(ANDROID, "Android");
        DICT_MAP.put(IOS, "iOS");
        DICT_MAP.put(WEIXIN, "微信");
        DICT_MAP.put(WEB, "PC WEB");
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
    private AppDeviceType() {
    }
}
