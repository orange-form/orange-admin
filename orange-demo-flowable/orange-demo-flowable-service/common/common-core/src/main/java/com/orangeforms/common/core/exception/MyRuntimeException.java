package com.orangeforms.common.core.exception;

/**
 * 自定义的运行时异常，在需要抛出运行时异常时，可使用该异常。
 * NOTE：主要是为了避免SonarQube进行代码质量扫描时，给出警告。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class MyRuntimeException extends RuntimeException {

    /**
     * 构造函数。
     */
    public MyRuntimeException() {

    }

    /**
     * 构造函数。
     *
     * @param throwable 引发异常对象。
     */
    public MyRuntimeException(Throwable throwable) {
        super(throwable);
    }

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public MyRuntimeException(String msg) {
        super(msg);
    }
}
