package com.orange.demo.upms.model;

import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.annotation.RelationDict;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * 权限资源实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_sys_perm")
public class SysPerm {

    /**
     * 权限资源Id。
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
     * 创建者Id。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新者Id。
     */
    @Column(name = "update_user_id")
    private Long updateUserId;

    /**
     * 更新时间。
     */
    @Column(name = "update_time")
    private Date updateTime;

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
