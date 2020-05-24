package com.orange.admin.upms.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.admin.common.core.annotation.DeletedFlagColumn;
import com.orange.admin.common.core.annotation.JobUpdateTimeColumn;
import com.orange.admin.common.core.annotation.RelationManyToMany;
import com.orange.admin.common.core.validator.ConstDictRef;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.constant.DataPermRuleType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;

/**
 * 数据权限实体对象。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Data
@Table(name = "zz_sys_data_perm")
public class SysDataPerm {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据权限Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "data_perm_id")
    private Long dataPermId;

    /**
     * 显示名称。
     */
    @NotBlank(message = "数据权限名称不能为空！")
    @Column(name = "data_perm_name")
    private String dataPermName;

    /**
     * 数据权限规则类型(0: 全部可见 1: 只看自己 2: 只看本部门 3: 本部门及子部门 4: 多部门及子部门 5: 自定义部门列表)。
     */
    @NotNull(message = "数据权限规则类型不能为空！")
    @ConstDictRef(constDictClass = DataPermRuleType.class)
    @Column(name = "rule_type")
    private Integer ruleType;

    /**
     * 创建者。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建者显示名称。
     */
    @Column(name = "create_username")
    private String createUsername;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间。
     */
    @JobUpdateTimeColumn
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @RelationManyToMany(
            relationMapperName = "sysDataPermDeptMapper",
            relationMasterIdField = "dataPermId",
            relationModelClass = SysDataPermDept.class)
    @Transient
    private List<SysDataPermDept> dataPermDeptList;

    @RelationManyToMany(
            relationMapperName = "sysDataPermMenuMapper",
            relationMasterIdField = "dataPermId",
            relationModelClass = SysDataPermMenu.class)
    @Transient
    private List<SysDataPermMenu> dataPermMenuList;

    @Transient
    private String createTimeStart;

    @Transient
    private String createTimeEnd;

    @Transient
    private String searchString;
}