package com.orange.demo.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.upms.model.constant.SysPermModuleType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

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
    @NotNull(message = "权限模块Id不能为空！", groups = {UpdateGroup.class})
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
    @NotBlank(message = "权限模块名称不能为空！")
    @Column(name = "module_name")
    private String moduleName;

    /**
     * 权限模块类型(0: 普通模块 1: Controller模块)。
     */
    @NotNull(message = "模块类型不能为空！")
    @ConstDictRef(constDictClass = SysPermModuleType.class, message = "数据验证失败，权限模块类型为无效值！")
    @Column(name = "module_type")
    private Integer moduleType;

    /**
     * 权限模块在当前层级下的顺序，由小到大。
     */
    @NotNull(message = "权限模块显示顺序不能为空！")
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

    @Transient
    private List<SysPerm> sysPermList;
}