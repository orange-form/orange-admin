package com.orange.demo.upmsservice.model;

import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.*;

/**
 * 权限模块实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "zz_sys_perm_module")
public class SysPermModule extends BaseModel {

    /**
     * 权限模块Id。
     */
    @Id
    @Column(name = "module_id")
    private Long moduleId;

    /**
     * 上级权限模块Id。
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 权限模块名称。
     */
    @Column(name = "module_name")
    private String moduleName;

    /**
     * 权限模块类型(0: 普通模块 1: Controller模块)。
     */
    @Column(name = "module_type")
    private Integer moduleType;

    /**
     * 权限模块在当前层级下的顺序，由小到大。
     */
    @Column(name = "show_order")
    private Integer showOrder;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @Transient
    private List<SysPerm> sysPermList;
}
