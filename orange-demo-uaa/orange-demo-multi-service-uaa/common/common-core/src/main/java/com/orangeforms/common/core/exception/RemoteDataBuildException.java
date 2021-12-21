package com.orangeforms.common.core.exception;

/**
 * 关联远程服务数据失败的自定义异常。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public class RemoteDataBuildException extends RuntimeException {

    /**
     * 构造函数。
     */
    public RemoteDataBuildException() {

    }

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public RemoteDataBuildException(String msg) {
        super(msg);
    }
}
