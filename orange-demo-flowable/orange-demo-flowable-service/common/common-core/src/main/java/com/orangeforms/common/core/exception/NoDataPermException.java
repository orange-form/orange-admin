package com.orangeforms.common.core.exception;

/**
 * 没有数据访问权限的自定义异常。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class NoDataPermException extends RuntimeException {

    /**
     * 构造函数。
     */
    public NoDataPermException() {

    }

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public NoDataPermException(String msg) {
        super(msg);
    }
}
