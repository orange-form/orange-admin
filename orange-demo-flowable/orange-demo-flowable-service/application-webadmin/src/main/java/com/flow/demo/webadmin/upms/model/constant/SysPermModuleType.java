package com.flow.demo.webadmin.upms.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限资源模块类型常量对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class SysPermModuleType {

    /**
     * 普通模块。
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * controller接口模块。
     */
    public static final int TYPE_CONTROLLER = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(TYPE_NORMAL, "普通模块");
        DICT_MAP.put(TYPE_CONTROLLER, "controller接口模块");
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
    private SysPermModuleType() {
    }
}
