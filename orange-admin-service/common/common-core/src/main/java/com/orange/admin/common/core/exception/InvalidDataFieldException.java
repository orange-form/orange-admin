package com.orange.admin.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 无效的实体对象字段的自定义异常。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidDataFieldException extends RuntimeException {

    private String modelName;
    private String fieldName;

    public InvalidDataFieldException(String modelName, String fieldName) {
        super("Invalid FieldName [" + fieldName + "] in Model Class [" + modelName + "].");
        this.modelName = modelName;
        this.fieldName = fieldName;
    }
}
