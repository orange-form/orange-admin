package com.orange.demo.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.Map;

/**
 * 权限资源实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("权限资源实体对象")
@Data
@Table(name = "zz_sys_perm")
public class SysPerm {

    /**
     * 权限资源Id。
     */
    @ApiModelProperty(value = "权限资源Id", required = true)
    @NotNull(message = "权限Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "perm_id")
    private Long permId;

    /**
     * 权限所在的权限模块Id。
     */
    @ApiModelProperty(value = "权限所在的权限模块Id", required = true)
    @NotNull(message = "权限模块Id不能为空！")
    @Column(name = "module_id")
    private Long moduleId;

    /**
     * 权限名称。
     */
    @ApiModelProperty(value = "权限名称", required = true)
    @NotBlank(message = "权限名称不能为空！")
    @Column(name = "perm_name")
    private String permName;

    /**
     * 关联的URL。
     */
    @ApiModelProperty(value = "关联的URL", required = true)
    @NotBlank(message = "权限关联的url不能为空！")
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @ApiModelProperty(value = "权限在当前模块下的顺序", required = true)
    @NotNull(message = "权限显示顺序不能为空！")
    @Column(name = "show_order")
    private Integer showOrder;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @ApiModelProperty(hidden = true)
    @RelationDict(
            masterIdField = "moduleId",
            slaveServiceName = "SysPermModuleService",
            slaveModelClass = SysPermModule.class,
            slaveIdField = "moduleId",
            slaveNameField = "moduleName")
    @Transient
    private Map<String, Object> moduleIdDictMap;
}