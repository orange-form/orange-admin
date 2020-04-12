package com.orange.admin.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 无效的实体对象的自定义异常。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidDataModelException extends RuntimeException {

    private String modelName;

    public InvalidDataModelException(String modelName) {
        super("Invalid Model Class [" + modelName + "].");
        this.modelName = modelName;
    }
}
