package com.orange.admin.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.admin.common.core.annotation.DeletedFlagColumn;
import com.orange.admin.common.core.annotation.RelationDict;
import com.orange.admin.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.Map;

/**
 * 权限资源实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_perm")
public class SysPerm {

    /**
     * 权限Id。
     */
    @NotNull(message = "权限Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "perm_id")
    private Long permId;

    /**
     * 权限所在的权限模块Id。
     */
    @NotNull(message = "权限模块Id不能为空！")
    @Column(name = "module_id")
    private Long moduleId;

    /**
     * 权限名称。
     */
    @NotBlank(message = "权限名称不能为空！")
    @Column(name = "perm_name")
    private String permName;

    /**
     * 关联的URL。
     */
    @NotBlank(message = "权限关联的url不能为空！")
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @NotNull(message = "权限显示顺序不能为空！")
    @Column(name = "show_order")
    private Integer showOrder;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @RelationDict(
            masterIdField = "moduleId",
            slaveServiceName = "SysPermModuleService",
            slaveModelClass = SysPermModule.class,
            slaveIdField = "moduleId",
            slaveNameField = "moduleName")
    @Transient
    private Map<String, Object> moduleIdDictMap;
}