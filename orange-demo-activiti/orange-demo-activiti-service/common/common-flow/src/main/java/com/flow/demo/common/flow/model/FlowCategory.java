package com.flow.demo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.common.flow.vo.FlowCategoryVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Date;

/**
 * 流程分类的实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_flow_category")
public class FlowCategory {

    /**
     * 主键Id。
     */
    @TableId(value = "category_id")
    private Long categoryId;

    /**
     * 显示名称。
     */
    @TableField(value = "name")
    private String name;

    /**
     * 分类编码。
     */
    @TableField(value = "code")
    private String code;

    /**
     * 实现顺序。
     */
    @TableField(value = "show_order")
    private Integer showOrder;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 更新者Id。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    @Mapper
    public interface FlowCategoryModelMapper extends BaseModelMapper<FlowCategoryVo, FlowCategory> {
    }
    public static final FlowCategoryModelMapper INSTANCE = Mappers.getMapper(FlowCategoryModelMapper.class);
}
