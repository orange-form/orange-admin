package com.orange.demo.upmsservice.config;

import com.orange.demo.common.core.constant.ApplicationConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示数据源类型的常量对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public final class DataSourceType {

    public static final int MAIN = 0;
    /**
     * 对于多数据源服务，操作日志的数据源类型是固定值。如果有冲突，可以直接修改
     * ApplicationConstant.OPERATION_LOG_DATASOURCE_TYPE的值。
     * 如果保存SysOperationLog操作日志的数据和其他业务位于同库，为了便于今后的
     * 迁移，这里也尽量要给其配置单独的数据源类型，今后数据库拆分时，可以直接修改
     * 该值对应的配置项即可。
     */
    public static final int OPERATION_LOG = ApplicationConstant.OPERATION_LOG_DATASOURCE_TYPE;

    private static final Map<String, Integer> TYPE_MAP = new HashMap<>(2);
    static {
        TYPE_MAP.put("main", MAIN);
        TYPE_MAP.put("operation-log", OPERATION_LOG);
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
