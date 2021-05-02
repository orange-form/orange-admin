package com.orange.demo.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 无效的Redis模式的自定义异常。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidRedisModeException extends RuntimeException {

    private final String mode;

    /**
     * 构造函数。
     *
     * @param mode 错误的模式。
     */
    public InvalidRedisModeException(String mode) {
        super("Invalid Redis Mode [" + mode + "], only supports [single/cluster/sentinel/master_slave]");
        this.mode = mode;
    }
}
