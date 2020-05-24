package com.orange.admin.common.core.constant;

/**
 * 数据记录逻辑删除标记常量。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public final class GlobalDeletedFlag {

    /**
     * 表示数据表记录已经删除
     */
    public static final int DELETED = -1;
    /**
     * 数据记录正常
     */
    public static final int NORMAL = 1;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private GlobalDeletedFlag() {
    }
}
