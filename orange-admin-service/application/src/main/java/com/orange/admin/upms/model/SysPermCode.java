package com.orange.admin.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.admin.common.core.annotation.DeletedFlagColumn;
import com.orange.admin.common.core.annotation.RelationManyToMany;
import com.orange.admin.common.core.validator.ConstDictRef;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.upms.model.constant.SysPermCodeType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * 权限字实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_perm_code")
public class SysPermCode {

    /**
     * 主键Id。
     */
    @NotNull(message = "权限字Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "perm_code_id")
    private Long permCodeId;

    /**
     * 上级权限字Id。
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 权限字标识(一般为有含义的英文字符串)。
     */
    @NotBlank(message = "权限字编码不能为空！")
    @Column(name = "perm_code")
    private String permCode;

    /**
     * 权限类型(0: 表单 1: UI片段 2: 操作)。
     */
    @NotNull(message = "权限字类型不能为空！")
    @ConstDictRef(constDictClass = SysPermCodeType.class, message = "数据验证失败，权限类型为无效值！")
    @Column(name = "perm_code_type")
    private Integer permCodeType;

    /**
     * 显示名称。
     */
    @NotBlank(message = "权限字显示名称不能为空！")
    @Column(name = "show_name")
    private String showName;

    /**
     * 显示顺序(数值越小，越靠前)。
     */
    @NotNull(message = "权限字显示顺序不能为空！")
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

    @RelationManyToMany(
            relationMapperName = "sysPermCodePermMapper",
            relationMasterIdField = "permCodeId",
            relationModelClass = SysPermCodePerm.class)
    @Transient
    private List<SysPermCodePerm> sysPermCodePermList;
}