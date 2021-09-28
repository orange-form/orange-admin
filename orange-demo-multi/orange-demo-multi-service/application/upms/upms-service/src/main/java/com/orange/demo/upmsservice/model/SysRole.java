package com.orange.demo.upmsservice.model;

import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.annotation.RelationManyToMany;
import com.orange.demo.common.core.base.model.BaseModel;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.upmsapi.vo.SysRoleVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.persistence.*;
import java.util.*;

/**
 * 角色实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "zz_sys_role")
public class SysRole extends BaseModel {

    /**
     * 主键Id。
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 角色名称。
     */
    @Column(name = "role_name")
    private String roleName;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @RelationManyToMany(
            relationMapperName = "sysRoleMenuMapper",
            relationMasterIdField = "roleId",
            relationModelClass = SysRoleMenu.class)
    @Transient
    private List<SysRoleMenu> sysRoleMenuList;

    @Mapper
    public interface SysRoleModelMapper extends BaseModelMapper<SysRoleVo, SysRole> {
        /**
         * 转换VO对象到实体对象。
         *
         * @param sysRoleVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysRoleMenuList", expression = "java(mapToBean(sysRoleVo.getSysRoleMenuList(), com.orange.demo.upmsservice.model.SysRoleMenu.class))")
        @Override
        SysRole toModel(SysRoleVo sysRoleVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param sysRole 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysRoleMenuList", expression = "java(beanToMap(sysRole.getSysRoleMenuList(), false))")
        @Override
        SysRoleVo fromModel(SysRole sysRole);
    }
    public static final SysRoleModelMapper INSTANCE = Mappers.getMapper(SysRole.SysRoleModelMapper.class);
}
