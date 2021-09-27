package com.flow.demo.common.flow.exception;

/**
 * 流程操作异常。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class FlowOperationException extends RuntimeException {

    /**
     * 构造函数。
     */
    public FlowOperationException() {

    }

    /**
     * 构造函数。
     *
     * @param throwable 引发异常对象。
     */
    public FlowOperationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public FlowOperationException(String msg) {
        super(msg);
    }
}
