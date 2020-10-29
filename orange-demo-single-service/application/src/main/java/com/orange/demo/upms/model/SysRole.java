package com.orange.demo.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.annotation.RelationManyToMany;
import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * 角色实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("角色实体对象")
@Data
@Table(name = "zz_sys_role")
public class SysRole {

    /**
     * 角色Id。
     */
    @ApiModelProperty(value = "角色Id", required = true)
    @NotNull(message = "角色Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 角色名称。
     */
    @ApiModelProperty(value = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空！")
    @Column(name = "role_name")
    private String roleName;

    /**
     * 创建者Id。
     */
    @ApiModelProperty(value = "创建者Id")
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建者显示名称。
     */
    @ApiModelProperty(value = "创建者显示名称")
    @Column(name = "create_username")
    private String createUsername;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @ApiModelProperty(hidden = true)
    @RelationManyToMany(
            relationMapperName = "sysRoleMenuMapper",
            relationMasterIdField = "roleId",
            relationModelClass = SysRoleMenu.class)
    @Transient
    private List<SysRoleMenu> sysRoleMenuList;

    @ApiModelProperty(value = "创建时间开始查询时间")
    @Transient
    private String createTimeStart;

    @ApiModelProperty(value = "创建时间结束查询时间")
    @Transient
    private String createTimeEnd;
}
