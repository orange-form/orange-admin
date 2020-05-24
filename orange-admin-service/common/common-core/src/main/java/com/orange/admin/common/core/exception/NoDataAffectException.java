package com.orange.admin.common.core.exception;

/**
 * 没有数据被修改的自定义异常。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
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
