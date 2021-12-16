package com.orangeforms.common.online.vo;

import lombok.Data;

/**
 * 在线数据表虚拟字段VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineVirtualColumnVo {

    /**
     * 主键Id。
     */
    private Long virtualColumnId;

    /**
     * 所在表Id。
     */
    private Long tableId;

    /**
     * 字段名称。
     */
    private String objectFieldName;

    /**
     * 属性类型。
     */
    private String objectFieldType;

    /**
     * 字段提示名。
     */
    private String columnPrompt;

    /**
     * 虚拟字段类型(0: 聚合)。
     */
    private Integer virtualType;

    /**
     * 关联数据源Id。
     */
    private Long datasourceId;

    /**
     * 关联Id。
     */
    private Long relationId;

    /**
     * 聚合字段所在关联表Id。
     */
    private Long aggregationTableId;

    /**
     * 关联表聚合字段Id。
     */
    private Long aggregationColumnId;

    /**
     * 聚合类型(0: count 1: sum 2: avg 3: max 4:min)。
     */
    private Integer aggregationType;

    /**
     * 存储过滤条件的json。
     */
    private String whereClauseJson;
}
