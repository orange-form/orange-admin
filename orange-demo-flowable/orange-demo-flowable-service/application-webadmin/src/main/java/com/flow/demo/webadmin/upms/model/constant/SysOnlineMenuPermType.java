package com.flow.demo.webadmin.upms.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单关联在线表单的控制权限类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class SysOnlineMenuPermType {

    /**
     * 查看。
     */
    public static final int TYPE_VIEW = 0;
    /**
     * 编辑。
     */
    public static final int TYPE_EDIT = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(4);
    static {
        DICT_MAP.put(TYPE_VIEW, "查看");
        DICT_MAP.put(TYPE_EDIT, "编辑");
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
    private SysOnlineMenuPermType() {
    }
}
