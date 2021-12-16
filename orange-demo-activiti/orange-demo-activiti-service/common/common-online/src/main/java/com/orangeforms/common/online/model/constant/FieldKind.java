package com.orangeforms.common.online.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 字段类别常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FieldKind {

    /**
     * 文件上传字段。
     */
    public static final int UPLOAD = 1;
    /**
     * 图片上传字段。
     */
    public static final int UPLOAD_IMAGE = 2;
    /**
     * 富文本字段。
     */
    public static final int RICH_TEXT = 3;
    /**
     * 创建时间字段。
     */
    public static final int CREATE_TIME = 20;
    /**
     * 创建人字段。
     */
    public static final int CREATE_USER_ID = 21;
    /**
     * 更新时间字段。
     */
    public static final int UPDATE_TIME = 22;
    /**
     * 更新人字段。
     */
    public static final int UPDATE_USER_ID = 23;
    /**
     * 逻辑删除字段。
     */
    public static final int LOGIC_DELETE = 31;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(9);
    static {
        DICT_MAP.put(UPLOAD, "文件上传字段");
        DICT_MAP.put(UPLOAD_IMAGE, "图片上传字段");
        DICT_MAP.put(RICH_TEXT, "富文本字段");
        DICT_MAP.put(CREATE_TIME, "创建时间字段");
        DICT_MAP.put(CREATE_USER_ID, "创建人字段");
        DICT_MAP.put(UPDATE_TIME, "更新时间字段");
        DICT_MAP.put(UPDATE_USER_ID, "更新人字段");
        DICT_MAP.put(LOGIC_DELETE, "逻辑删除字段");
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
    private FieldKind() {
    }
}
