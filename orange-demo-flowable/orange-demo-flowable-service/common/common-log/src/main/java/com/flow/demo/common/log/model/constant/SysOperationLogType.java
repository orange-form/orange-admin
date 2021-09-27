package com.flow.demo.common.log.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志类型常量字典对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class SysOperationLogType {

    /**
     * 其他。
     */
    public static final int OTHER = -1;
    /**
     * 登录。
     */
    public static final int LOGIN = 0;
    /**
     * 登出。
     */
    public static final int LOGOUT = 5;
    /**
     * 新增。
     */
    public static final int ADD = 10;
    /**
     * 修改。
     */
    public static final int UPDATE = 15;
    /**
     * 删除。
     */
    public static final int DELETE = 20;
    /**
     * 新增多对多关联。
     */
    public static final int ADD_M2M = 25;
    /**
     * 移除多对多关联。
     */
    public static final int DELETE_M2M = 30;
    /**
     * 查询。
     */
    public static final int LIST = 35;
    /**
     * 分组查询。
     */
    public static final int LIST_WITH_GROUP = 40;
    /**
     * 导出。
     */
    public static final int EXPORT = 45;
    /**
     * 上传。
     */
    public static final int UPLOAD = 50;
    /**
     * 下载。
     */
    public static final int DOWNLOAD = 55;
    /**
     * 重置缓存。
     */
    public static final int RELOAD_CACHE = 60;
    /**
     * 发布。
     */
    public static final int PUBLISH = 65;
    /**
     * 取消发布。
     */
    public static final int UNPUBLISH = 70;
    /**
     * 暂停。
     */
    public static final int SUSPEND = 75;
    /**
     * 恢复。
     */
    public static final int RESUME = 80;
    /**
     * 启动流程。
     */
    public static final int START_PROCESS = 100;
    /**
     * 停止流程。
     */
    public static final int STOP_PROCESS = 105;
    /**
     * 删除流程。
     */
    public static final int DELETE_PROCESS = 110;
    /**
     * 取消流程。
     */
    public static final int CANCEL_PROCESS = 115;
    /**
     * 提交任务。
     */
    public static final int SUBMIT_TASK = 120;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(15);
    static {
        DICT_MAP.put(OTHER, "其他");
        DICT_MAP.put(LOGIN, "登录");
        DICT_MAP.put(LOGOUT, "登出");
        DICT_MAP.put(ADD, "新增");
        DICT_MAP.put(UPDATE, "修改");
        DICT_MAP.put(DELETE, "删除");
        DICT_MAP.put(ADD_M2M, "新增多对多关联");
        DICT_MAP.put(DELETE_M2M, "移除多对多关联");
        DICT_MAP.put(LIST, "查询");
        DICT_MAP.put(LIST_WITH_GROUP, "分组查询");
        DICT_MAP.put(EXPORT, "导出");
        DICT_MAP.put(UPLOAD, "上传");
        DICT_MAP.put(DOWNLOAD, "下载");
        DICT_MAP.put(RELOAD_CACHE, "重置缓存");
        DICT_MAP.put(PUBLISH, "发布");
        DICT_MAP.put(UNPUBLISH, "取消发布");
        DICT_MAP.put(SUSPEND, "暂停");
        DICT_MAP.put(RESUME, "恢复");
        DICT_MAP.put(START_PROCESS, "启动流程");
        DICT_MAP.put(STOP_PROCESS, "停止流程");
        DICT_MAP.put(DELETE_PROCESS, "删除流程");
        DICT_MAP.put(CANCEL_PROCESS, "取消流程");
        DICT_MAP.put(SUBMIT_TASK, "提交任务");
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
    private SysOperationLogType() {
    }
}
