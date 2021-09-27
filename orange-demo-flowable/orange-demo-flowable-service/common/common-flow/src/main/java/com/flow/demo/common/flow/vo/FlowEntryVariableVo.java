package com.flow.demo.common.flow.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流程变量Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowEntryVariableVo {

    /**
     * 主键Id。
     */
    private Long variableId;

    /**
     * 流程Id。
     */
    private Long entryId;

    /**
     * 变量名。
     */
    private String variableName;

    /**
     * 显示名。
     */
    private String showName;

    /**
     * 变量类型。
     */
    private Integer variableType;

    /**
     * 绑定数据源Id。
     */
    private Long bindDatasourceId;

    /**
     * 绑定数据源关联Id。
     */
    private Long bindRelationId;

    /**
     * 绑定字段Id。
     */
    private Long bindColumnId;

    /**
     * 是否内置。
     */
    private Boolean builtin;

    /**
     * 创建时间。
     */
    private Date createTime;
}
