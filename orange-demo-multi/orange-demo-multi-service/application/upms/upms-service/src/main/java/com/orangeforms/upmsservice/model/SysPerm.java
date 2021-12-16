package com.orangeforms.upmsservice.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.base.model.BaseModel;
import com.orangeforms.common.core.annotation.RelationDict;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * 权限资源实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "zz_sys_perm")
public class SysPerm extends BaseModel {

    /**
     * 权限Id。
     */
    @TableId(value = "perm_id")
    private Long permId;

    /**
     * 权限所在的权限模块Id。
     */
    @TableField(value = "module_id")
    private Long moduleId;

    /**
     * 权限名称。
     */
    @TableField(value = "perm_name")
    private String permName;

    /**
     * 关联的URL。
     */
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @TableField(value = "show_order")
    private Integer showOrder;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    @RelationDict(
            masterIdField = "moduleId",
            slaveServiceName = "SysPermModuleService",
            slaveModelClass = SysPermModule.class,
            slaveIdField = "moduleId",
            slaveNameField = "moduleName")
    @TableField(exist = false)
    private Map<String, Object> moduleIdDictMap;
}
