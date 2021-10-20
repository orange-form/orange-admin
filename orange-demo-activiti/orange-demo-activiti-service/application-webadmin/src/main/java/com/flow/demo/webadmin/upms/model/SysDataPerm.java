package com.flow.demo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.common.core.util.MyCommonUtil;
import com.flow.demo.common.core.annotation.RelationManyToMany;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.webadmin.upms.vo.SysDataPermVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;

/**
 * 数据权限实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_data_perm")
public class SysDataPerm {

    /**
     * 主键Id。
     */
    @TableId(value = "data_perm_id")
    private Long dataPermId;

    /**
     * 显示名称。
     */
    @TableField(value = "data_perm_name")
    private String dataPermName;

    /**
     * 数据权限规则类型(0: 全部可见 1: 只看自己 2: 只看本部门 3: 本部门及子部门 4: 多部门及子部门 5: 自定义部门列表)。
     */
    @TableField(value = "rule_type")
    private Integer ruleType;

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新者Id。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    @TableField(exist = false)
    private String deptIdListString;

    @RelationManyToMany(
            relationMapperName = "sysDataPermDeptMapper",
            relationMasterIdField = "dataPermId",
            relationModelClass = SysDataPermDept.class)
    @TableField(exist = false)
    private List<SysDataPermDept> dataPermDeptList;

    @TableField(exist = false)
    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = MyCommonUtil.replaceSqlWildcard(searchString);
    }

    @Mapper
    public interface SysDataPermModelMapper extends BaseModelMapper<SysDataPermVo, SysDataPerm> {
        /**
         * 转换VO对象到实体对象。
         *
         * @param sysDataPermVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "dataPermDeptList", expression = "java(mapToBean(sysDataPermVo.getDataPermDeptList(), com.flow.demo.webadmin.upms.model.SysDataPermDept.class))")
        @Override
        SysDataPerm toModel(SysDataPermVo sysDataPermVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param sysDataPerm 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "dataPermDeptList", expression = "java(beanToMap(sysDataPerm.getDataPermDeptList(), false))")
        @Override
        SysDataPermVo fromModel(SysDataPerm sysDataPerm);
    }
    public static final SysDataPermModelMapper INSTANCE = Mappers.getMapper(SysDataPerm.SysDataPermModelMapper.class);
}
