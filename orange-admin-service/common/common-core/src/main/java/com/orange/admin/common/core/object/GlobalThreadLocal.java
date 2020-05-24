package com.orange.admin.common.core.object;

import cn.hutool.core.util.BooleanUtil;

/**
 * 线程本地化数据管理的工具类。可根据需求自行添加更多的线程本地化变量及其操作方法。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public class GlobalThreadLocal {

    /**
     * 存储数据权限过滤是否启用的线程本地化对象。
     */
    private static final ThreadLocal<Boolean> DATA_PERM_ENABLE = ThreadLocal.withInitial(() -> Boolean.TRUE);

    /**
     * 设置数据权限过滤是否打开。如果打开，当前Servlet线程所执行的SQL操作，均会进行数据权限过滤。
     *
     * @param enable 打开为true，否则false。
     * @return 返回之前的状态，便于恢复。
     */
    public static boolean setDataPerm(boolean enable) {
        boolean oldValue = DATA_PERM_ENABLE.get();
        DATA_PERM_ENABLE.set(enable);
        return oldValue;
    }

    /**
     * 判断当前Servlet线程所执行的SQL操作，是否进行数据权限过滤。
     *
     * @return true 进行数据权限过滤，否则false。
     */
    public static boolean enabledDataPerm() {
        return BooleanUtil.isTrue(DATA_PERM_ENABLE.get());
    }

    /**
     * 清空该存储数据，主动释放线程本地化存储资源。
     */
    public static void clearDataPerm() {
        DATA_PERM_ENABLE.remove();
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private GlobalThreadLocal() {
    }
}
