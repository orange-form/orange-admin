package com.flow.demo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.common.core.annotation.RelationConstDict;
import com.flow.demo.common.core.annotation.RelationDict;
import com.flow.demo.common.core.annotation.RelationOneToOne;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.common.online.model.constant.RelationType;
import com.flow.demo.common.online.vo.OnlineDatasourceRelationVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单的数据源关联实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_datasource_relation")
public class OnlineDatasourceRelation {

    /**
     * 主键Id。
     */
    @TableId(value = "relation_id")
    private Long relationId;

    /**
     * 关联名称。
     */
    @TableField(value = "relation_name")
    private String relationName;

    /**
     * 变量名。
     */
    @TableField(value = "variable_name")
    private String variableName;

    /**
     * 主数据源Id。
     */
    @TableField(value = "datasource_id")
    private Long datasourceId;

    /**
     * 关联类型。
     */
    @TableField(value = "relation_type")
    private Integer relationType;

    /**
     * 主表关联字段Id。
     */
    @TableField(value = "master_column_id")
    private Long masterColumnId;

    /**
     * 从表Id。
     */
    @TableField(value = "slave_table_id")
    private Long slaveTableId;

    /**
     * 从表关联字段Id。
     */
    @TableField(value = "slave_column_id")
    private Long slaveColumnId;

    /**
     * 删除主表的时候是否级联删除一对一和一对多的从表数据，多对多只是删除关联，不受到这个标记的影响。。
     */
    @TableField(value = "cascade_delete")
    private Boolean cascadeDelete;

    /**
     * 是否左连接。
     */
    @TableField(value = "left_join")
    private Boolean leftJoin;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    @RelationOneToOne(
            masterIdField = "masterColumnId",
            slaveServiceName = "onlineColumnService",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId")
    @TableField(exist = false)
    private OnlineColumn masterColumn;

    @RelationOneToOne(
            masterIdField = "slaveTableId",
            slaveServiceName = "onlineTableService",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId")
    @TableField(exist = false)
    private OnlineTable slaveTable;

    @RelationOneToOne(
            masterIdField = "slaveColumnId",
            slaveServiceName = "onlineColumnService",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId")
    @TableField(exist = false)
    private OnlineColumn slaveColumn;

    @RelationDict(
            masterIdField = "masterColumnId",
            slaveServiceName = "onlineColumnService",
            equalOneToOneRelationField = "onlineColumn",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId",
            slaveNameField = "columnName")
    @TableField(exist = false)
    private Map<String, Object> masterColumnIdDictMap;

    @RelationDict(
            masterIdField = "slaveTableId",
            slaveServiceName = "onlineTableService",
            equalOneToOneRelationField = "onlineTable",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId",
            slaveNameField = "modelName")
    @TableField(exist = false)
    private Map<String, Object> slaveTableIdDictMap;

    @RelationDict(
            masterIdField = "slaveColumnId",
            slaveServiceName = "onlineColumnService",
            equalOneToOneRelationField = "onlineColumn",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId",
            slaveNameField = "columnName")
    @TableField(exist = false)
    private Map<String, Object> slaveColumnIdDictMap;

    @RelationConstDict(
            masterIdField = "relationType",
            constantDictClass = RelationType.class)
    @TableField(exist = false)
    private Map<String, Object> relationTypeDictMap;

    @Mapper
    public interface OnlineDatasourceRelationModelMapper
            extends BaseModelMapper<OnlineDatasourceRelationVo, OnlineDatasourceRelation> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param onlineDatasourceRelationVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "masterColumn", expression = "java(mapToBean(onlineDatasourceRelationVo.getMasterColumn(), com.flow.demo.common.online.model.OnlineColumn.class))")
        @Mapping(target = "slaveTable", expression = "java(mapToBean(onlineDatasourceRelationVo.getSlaveTable(), com.flow.demo.common.online.model.OnlineTable.class))")
        @Mapping(target = "slaveColumn", expression = "java(mapToBean(onlineDatasourceRelationVo.getSlaveColumn(), com.flow.demo.common.online.model.OnlineColumn.class))")
        @Override
        OnlineDatasourceRelation toModel(OnlineDatasourceRelationVo onlineDatasourceRelationVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param onlineDatasourceRelation 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "masterColumn", expression = "java(beanToMap(onlineDatasourceRelation.getMasterColumn(), false))")
        @Mapping(target = "slaveTable", expression = "java(beanToMap(onlineDatasourceRelation.getSlaveTable(), false))")
        @Mapping(target = "slaveColumn", expression = "java(beanToMap(onlineDatasourceRelation.getSlaveColumn(), false))")
        @Override
        OnlineDatasourceRelationVo fromModel(OnlineDatasourceRelation onlineDatasourceRelation);
    }
    public static final OnlineDatasourceRelationModelMapper INSTANCE = Mappers.getMapper(OnlineDatasourceRelationModelMapper.class);
}
