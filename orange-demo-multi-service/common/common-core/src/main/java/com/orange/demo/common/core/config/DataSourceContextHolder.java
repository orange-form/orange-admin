package com.orange.demo.common.core.config;

/**
 * 通过线程本地存储的方式，保存当前数据库操作所需的数据源类型，动态数据源会根据该值，进行动态切换。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<Integer> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源类型
     * @param type 数据源类型
     */
    public static void setDataSourceType(Integer type) {
        CONTEXT_HOLDER.set(type);
    }

    /**
     * 获取当前数据库操作执行线程的数据源类型，同时由动态数据源的路由函数调用。
     * @return 数据源类型。
     */
    public static Integer getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除线程本地变量，以免内存泄漏
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private DataSourceContextHolder() {
    }
}
