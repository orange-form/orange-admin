package com.orange.demo.common.core.exception;

/**
 * 没有数据被修改的自定义异常。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public class NoDataAffectException extends RuntimeException {

    /**
     * 构造函数。
     */
	public NoDataAffectException() {

	}

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
	public NoDataAffectException(String msg) {
		super(msg);
	}
}
