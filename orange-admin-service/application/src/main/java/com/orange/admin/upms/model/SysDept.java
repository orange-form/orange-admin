package com.orange.admin.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.admin.common.core.annotation.DeletedFlagColumn;
import com.orange.admin.common.core.annotation.JobUpdateTimeColumn;
import com.orange.admin.common.core.validator.UpdateGroup;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;

/**
 * SysDept实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_dept")
public class SysDept {

    /**
     * 部门Id。
     */
    @NotNull(message = "数据验证失败，部门Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "dept_id")
    private Long deptId;

    /**
     * 部门名称。
     */
    @NotBlank(message = "数据验证失败，部门名称不能为空！")
    @Column(name = "dept_name")
    private String deptName;

    /**
     * 显示顺序。
     */
    @NotNull(message = "数据验证失败，显示顺序不能为空！")
    @Column(name = "show_order")
    private Integer showOrder;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    /**
     * 创建用户Id。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建用户名。
     */
    @Column(name = "create_username")
    private String createUsername;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间。
     */
    @JobUpdateTimeColumn
    @Column(name = "update_time")
    private Date updateTime;
}
