package com.orangeforms.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.common.online.vo.OnlineVirtualColumnVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * 在线数据表虚拟字段实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_virtual_column")
public class OnlineVirtualColumn {

    /**
     * 主键Id。
     */
    @TableId(value = "virtual_column_id")
    private Long virtualColumnId;

    /**
     * 所在表Id。
     */
    @TableField(value = "table_id")
    private Long tableId;

    /**
     * 字段名称。
     */
    @TableField(value = "object_field_name")
    private String objectFieldName;

    /**
     * 属性类型。
     */
    @TableField(value = "object_field_type")
    private String objectFieldType;

    /**
     * 字段提示名。
     */
    @TableField(value = "column_prompt")
    private String columnPrompt;

    /**
     * 虚拟字段类型(0: 聚合)。
     */
    @TableField(value = "virtual_type")
    private Integer virtualType;

    /**
     * 关联数据源Id。
     */
    @TableField(value = "datasource_id")
    private Long datasourceId;

    /**
     * 关联Id。
     */
    @TableField(value = "relation_id")
    private Long relationId;

    /**
     * 聚合字段所在关联表Id。
     */
    @TableField(value = "aggregation_table_id")
    private Long aggregationTableId;

    /**
     * 关联表聚合字段Id。
     */
    @TableField(value = "aggregation_column_id")
    private Long aggregationColumnId;

    /**
     * 聚合类型(0: count 1: sum 2: avg 3: max 4:min)。
     */
    @TableField(value = "aggregation_type")
    private Integer aggregationType;

    /**
     * 存储过滤条件的json。
     */
    @TableField(value = "where_clause_json")
    private String whereClauseJson;

    @Mapper
    public interface OnlineVirtualColumnModelMapper extends BaseModelMapper<OnlineVirtualColumnVo, OnlineVirtualColumn> {
    }
    public static final OnlineVirtualColumnModelMapper INSTANCE = Mappers.getMapper(OnlineVirtualColumnModelMapper.class);
}
