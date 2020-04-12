package com.orange.admin.common.core.exception;

/**
 * 没有数据被修改的自定义异常。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public class NoDataAffectException extends RuntimeException {

	public NoDataAffectException() {

	}

	public NoDataAffectException(String msg) {
		super(msg);
	}
}
