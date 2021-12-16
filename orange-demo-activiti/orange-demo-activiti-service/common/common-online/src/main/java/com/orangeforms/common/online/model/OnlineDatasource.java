package com.orangeforms.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.annotation.RelationDict;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.common.online.vo.OnlineDatasourceVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单的数据源实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_datasource")
public class OnlineDatasource {

    /**
     * 主键Id。
     */
    @TableId(value = "datasource_id")
    private Long datasourceId;

    /**
     * 数据源名称。
     */
    @TableField(value = "datasource_name")
    private String datasourceName;

    /**
     * 数据源变量名，会成为数据访问url的一部分。
     */
    @TableField(value = "variable_name")
    private String variableName;

    /**
     * 数据库链接Id。
     */
    @TableField(value = "dblink_id")
    private Long dblinkId;

    /**
     * 主表Id。
     */
    @TableField(value = "master_table_id")
    private Long masterTableId;

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

    /**
     * datasourceId 的多对多关联表数据对象。
     */
    @TableField(exist = false)
    private OnlinePageDatasource onlinePageDatasource;

    @RelationDict(
            masterIdField = "masterTableId",
            slaveServiceName = "onlineTableService",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId",
            slaveNameField = "tableName")
    @TableField(exist = false)
    private Map<String, Object> masterTableIdDictMap;

    @TableField(exist = false)
    private OnlineTable masterTable;

    @Mapper
    public interface OnlineDatasourceModelMapper extends BaseModelMapper<OnlineDatasourceVo, OnlineDatasource> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param onlineDatasourceVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "onlinePageDatasource", expression = "java(mapToBean(onlineDatasourceVo.getOnlinePageDatasource(), com.orangeforms.common.online.model.OnlinePageDatasource.class))")
        @Override
        OnlineDatasource toModel(OnlineDatasourceVo onlineDatasourceVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param onlineDatasource 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "onlinePageDatasource", expression = "java(beanToMap(onlineDatasource.getOnlinePageDatasource(), false))")
        @Override
        OnlineDatasourceVo fromModel(OnlineDatasource onlineDatasource);
    }
    public static final OnlineDatasourceModelMapper INSTANCE = Mappers.getMapper(OnlineDatasourceModelMapper.class);
}
