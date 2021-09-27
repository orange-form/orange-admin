package com.flow.demo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.webadmin.upms.vo.SysDeptVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;

/**
 * SysDept实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_dept")
public class SysDept {

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

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

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
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @Mapper
    public interface SysDeptModelMapper extends BaseModelMapper<SysDeptVo, SysDept> {
    }
    public static final SysDeptModelMapper INSTANCE = Mappers.getMapper(SysDeptModelMapper.class);
}
