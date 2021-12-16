package com.orangeforms.upmsservice.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.upmsapi.vo.SysDeptVo;
import com.orangeforms.common.core.base.model.BaseModel;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * SysDept实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "zz_sys_dept")
public class SysDept extends BaseModel {

    /**
     * 部门Id。
     */
    @TableId(value = "dept_id")
    private Long deptId;

    /**
     * 部门名称。
     */
    @TableField(value = "dept_name")
    private String deptName;

    /**
     * 显示顺序。
     */
    @TableField(value = "show_order")
    private Integer showOrder;

    /**
     * 父部门Id。
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    @Mapper
    public interface SysDeptModelMapper extends BaseModelMapper<SysDeptVo, SysDept> {
    }
    public static final SysDeptModelMapper INSTANCE = Mappers.getMapper(SysDeptModelMapper.class);
}
