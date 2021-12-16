package com.orangeforms.apidoc.tools.exception;

/**
 * 解析Mybatis XML Mapper中的异常。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class MapperParseException extends RuntimeException {

    /**
     * 构造函数。
     */
    public MapperParseException() {

    }

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public MapperParseException(String msg) {
        super(msg);
    }

}
