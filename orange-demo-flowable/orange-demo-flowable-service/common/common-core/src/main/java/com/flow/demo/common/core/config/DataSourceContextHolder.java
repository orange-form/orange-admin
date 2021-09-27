package com.flow.demo.common.core.config;

/**
 * 通过线程本地存储的方式，保存当前数据库操作所需的数据源类型，动态数据源会根据该值，进行动态切换。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<Integer> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源类型。
     *
     * @param type 数据源类型
     * @return 原有数据源类型，如果第一次设置则返回null。
     */
    public static Integer setDataSourceType(Integer type) {
        Integer datasourceType = CONTEXT_HOLDER.get();
        CONTEXT_HOLDER.set(type);
        return datasourceType;
    }

    /**
     * 获取当前数据库操作执行线程的数据源类型，同时由动态数据源的路由函数调用。
     *
     * @return 数据源类型。
     */
    public static Integer getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除线程本地变量，以免内存泄漏。

     * @param originalType 原有的数据源类型，如果该值为null，则情况本地化变量。
     */
    public static void unset(Integer originalType) {
        if (originalType == null) {
            CONTEXT_HOLDER.remove();
        } else {
            CONTEXT_HOLDER.set(originalType);
        }
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private DataSourceContextHolder() {
    }
}
