package com.orange.demo.upms.model;

import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * 权限模块实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_sys_perm_module")
public class SysPermModule {

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

    @Transient
    private List<SysPerm> sysPermList;
}
