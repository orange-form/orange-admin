package com.orange.admin.common.core.constant;

/**
 * 数据记录逻辑删除标记常量。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public interface GlobalDeletedFlag {

    /**
     * 表示数据表记录已经删除
     */
    int DELETED = -1;
    /**
     * 数据记录正常
     */
    int NORMAL = 1;
}
