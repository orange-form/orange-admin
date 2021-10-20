package com.flow.demo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.common.online.vo.OnlineDblinkVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Date;

/**
 * 在线表单数据表所在数据库链接实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_dblink")
public class OnlineDblink {

    /**
     * 主键Id。
     */
    @TableId(value = "dblink_id")
    private Long dblinkId;

    /**
     * 链接中文名称。
     */
    @TableField(value = "dblink_name")
    private String dblinkName;

    /**
     * 链接英文名称。
     */
    @TableField(value = "variable_name")
    private String variableName;

    /**
     * 链接描述。
     */
    @TableField(value = "dblink_desc")
    private String dblinkDesc;

    /**
     * 数据源配置常量。
     */
    @TableField(value = "dblink_config_constant")
    private Integer dblinkConfigConstant;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    @Mapper
    public interface OnlineDblinkModelMapper extends BaseModelMapper<OnlineDblinkVo, OnlineDblink> {
    }
    public static final OnlineDblinkModelMapper INSTANCE = Mappers.getMapper(OnlineDblinkModelMapper.class);
}
