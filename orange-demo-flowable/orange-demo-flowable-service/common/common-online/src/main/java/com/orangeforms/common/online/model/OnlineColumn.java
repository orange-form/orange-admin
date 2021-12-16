package com.orangeforms.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.annotation.RelationConstDict;
import com.orangeforms.common.core.annotation.RelationOneToOne;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.common.online.model.constant.FieldKind;
import com.orangeforms.common.online.vo.OnlineColumnVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单数据表字段实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_column")
public class OnlineColumn {

    /**
     * 主键Id。
     */
    @TableId(value = "column_id")
    private Long columnId;

    /**
     * 字段名。
     */
    @TableField(value = "column_name")
    private String columnName;

    /**
     * 数据表Id。
     */
    @TableField(value = "table_id")
    private Long tableId;

    /**
     * 数据表中的字段类型。
     */
    @TableField(value = "column_type")
    private String columnType;

    /**
     * 数据表中的完整字段类型(包括了精度和刻度)。
     */
    @TableField(value = "full_column_type")
    private String fullColumnType;

    /**
     * 是否为主键。
     */
    @TableField(value = "primary_key")
    private Boolean primaryKey;

    /**
     * 是否是自增主键(0: 不是 1: 是)。
     */
    @TableField(value = "auto_increment")
    private Boolean autoIncrement;

    /**
     * 是否可以为空 (0: 不可以为空 1: 可以为空)。
     */
    @TableField(value = "nullable")
    private Boolean nullable;

    /**
     * 缺省值。
     */
    @TableField(value = "column_default")
    private String columnDefault;

    /**
     * 字段在数据表中的显示位置。
     */
    @TableField(value = "column_show_order")
    private Integer columnShowOrder;

    /**
     * 数据表中的字段注释。
     */
    @TableField(value = "column_comment")
    private String columnComment;

    /**
     * 对象映射字段名称。
     */
    @TableField(value = "object_field_name")
    private String objectFieldName;

    /**
     * 对象映射字段类型。
     */
    @TableField(value = "object_field_type")
    private String objectFieldType;

    /**
     * 过滤字段类型。
     */
    @TableField(value = "filter_type")
    private Integer filterType;

    /**
     * 是否是主键的父Id。
     */
    @TableField(value = "parent_key")
    private Boolean parentKey;

    /**
     * 是否部门过滤字段。
     */
    @TableField(value = "dept_filter")
    private Boolean deptFilter;

    /**
     * 是否用户过滤字段。
     */
    @TableField(value = "user_filter")
    private Boolean userFilter;

    /**
     * 字段类别。
     */
    @TableField(value = "field_kind")
    private Integer fieldKind;

    /**
     * 包含的文件文件数量，0表示无限制。
     */
    @TableField(value = "max_file_count")
    private Integer maxFileCount;

    /**
     * 字典Id。
     */
    @TableField(value = "dict_id")
    private Long dictId;

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

    @RelationConstDict(
            masterIdField = "fieldKind",
            constantDictClass = FieldKind.class)
    @TableField(exist = false)
    private Map<String, Object> fieldKindDictMap;

    @RelationOneToOne(
            masterIdField = "dictId",
            slaveServiceName = "OnlineDictService",
            slaveIdField = "dictId",
            slaveModelClass = OnlineDict.class,
            loadSlaveDict = false)
    @TableField(exist = false)
    private OnlineDict dictInfo;

    @Mapper
    public interface OnlineColumnModelMapper extends BaseModelMapper<OnlineColumnVo, OnlineColumn> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param onlineColumnVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "dictInfo", expression = "java(mapToBean(onlineColumnVo.getDictInfo(), com.orangeforms.common.online.model.OnlineDict.class))")
        @Override
        OnlineColumn toModel(OnlineColumnVo onlineColumnVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param onlineColumn 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "dictInfo", expression = "java(beanToMap(onlineColumn.getDictInfo(), false))")
        @Override
        OnlineColumnVo fromModel(OnlineColumn onlineColumn);
    }
    public static final OnlineColumnModelMapper INSTANCE = Mappers.getMapper(OnlineColumnModelMapper.class);
}
