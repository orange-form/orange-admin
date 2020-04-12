package com.orange.admin.common.core.exception;

/**
 * 数据验证失败的自定义异常。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public class DataValidationException extends RuntimeException {

    public DataValidationException() {

    }

    public DataValidationException(String msg) {
        super(msg);
    }
}
