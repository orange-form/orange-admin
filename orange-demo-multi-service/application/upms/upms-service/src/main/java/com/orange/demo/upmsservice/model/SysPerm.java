package com.orange.demo.upmsservice.model;

import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.base.model.BaseModel;
import com.orange.demo.common.core.annotation.RelationDict;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

/**
 * 权限资源实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "zz_sys_perm")
public class SysPerm extends BaseModel {

    /**
     * 权限Id。
     */
    @Id
    @Column(name = "perm_id")
    private Long permId;

    /**
     * 权限所在的权限模块Id。
     */
    @Column(name = "module_id")
    private Long moduleId;

    /**
     * 权限名称。
     */
    @Column(name = "perm_name")
    private String permName;

    /**
     * 关联的URL。
     */
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @Column(name = "show_order")
    private Integer showOrder;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
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
