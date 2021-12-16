package com.orangeforms.application.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生行为常量字典对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public final class StudentActionType {

    /**
     * 充值。
     */
    public static final int RECHARGE = 0;
    /**
     * 购课。
     */
    public static final int BUY_COURSE = 1;
    /**
     * 上课签到。
     */
    public static final int SIGNIN_COURSE = 2;
    /**
     * 上课签退。
     */
    public static final int SIGNOUT_COURSE = 3;
    /**
     * 看视频课。
     */
    public static final int WATCH_VIDEO = 4;
    /**
     * 做作业。
     */
    public static final int DO_PAPER = 5;
    /**
     * 刷题。
     */
    public static final int REFRESH_EXERCISE = 6;
    /**
     * 献花。
     */
    public static final int PRESENT_FLOWER = 7;
    /**
     * 购买视频。
     */
    public static final int BUY_VIDEO_COURSE = 8;
    /**
     * 购买鲜花。
     */
    public static final int BUY_FLOWER = 9;
    /**
     * 购买作业。
     */
    public static final int BUY_PAPER = 10;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(11);
    static {
        DICT_MAP.put(RECHARGE, "充值");
        DICT_MAP.put(BUY_COURSE, "购课");
        DICT_MAP.put(SIGNIN_COURSE, "上课签到");
        DICT_MAP.put(SIGNOUT_COURSE, "上课签退");
        DICT_MAP.put(WATCH_VIDEO, "看视频课");
        DICT_MAP.put(DO_PAPER, "做作业");
        DICT_MAP.put(REFRESH_EXERCISE, "刷题");
        DICT_MAP.put(PRESENT_FLOWER, "献花");
        DICT_MAP.put(BUY_VIDEO_COURSE, "购买视频");
        DICT_MAP.put(BUY_FLOWER, "购买鲜花");
        DICT_MAP.put(BUY_PAPER, "购买作业");
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
    private StudentActionType() {
    }
}
