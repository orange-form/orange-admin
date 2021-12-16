package com.orangeforms.webadmin.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示数据源类型的常量对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class DataSourceType {

    public static final int MAIN = 0;

    private static final Map<String, Integer> TYPE_MAP = new HashMap<>(2);
    static {
        TYPE_MAP.put("main", MAIN);
    }

    /**
     * 根据名称获取字典类型。
     *
     * @param name 数据源在配置中的名称。
     * @return 返回可用于多数据源切换的数据源类型。
     */
    public static Integer getDataSourceTypeByName(String name) {
        return TYPE_MAP.get(name);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private DataSourceType() {
    }
}
