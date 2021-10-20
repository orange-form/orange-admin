package com.flow.demo.common.core.util;

/**
 * 拼接日志消息的工具类。
 * 主要目标是，尽量保证日志输出的统一性，同时也可以有效减少与日志信息相关的常量字符串，
 * 提高代码的规范度和可维护性。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class LogMessageUtil {

    /**
     * RPC调用错误格式。
     */
    private static final String RPC_ERROR_MSG_FORMAT = "RPC Failed with Error message [%s]";

    /**
     * 组装RPC调用的错误信息。
     *
     * @param errorMsg 具体的错误信息。
     * @return 格式化后的错误信息。
     */
    public static String makeRpcError(String errorMsg) {
        return String.format(RPC_ERROR_MSG_FORMAT, errorMsg);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private LogMessageUtil() {
    }
}
