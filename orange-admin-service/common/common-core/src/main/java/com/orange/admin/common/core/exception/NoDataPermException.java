package com.orange.admin.common.core.exception;

/**
 * 没有数据访问权限的自定义异常。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public class NoDataPermException extends RuntimeException {

    public NoDataPermException() {

    }

    public NoDataPermException(String msg) {
        super(msg);
    }
}
