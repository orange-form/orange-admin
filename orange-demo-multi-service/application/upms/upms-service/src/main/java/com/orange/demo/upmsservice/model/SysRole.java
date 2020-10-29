package com.orange.demo.upmsservice.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.annotation.RelationManyToMany;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.upmsinterface.dto.SysRoleDto;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * 角色实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_sys_role")
public class SysRole {

    /**
     * 主键Id。
     */
    @NotNull(message = "角色Id不能为空！")
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 角色名称。
     */
    @NotBlank(message = "角色名称不能为空！")
    @Column(name = "role_name")
    private String roleName;

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
            relationMapperName = "sysRoleMenuMapper",
            relationMasterIdField = "roleId",
            relationModelClass = SysRoleMenu.class)
    @Transient
    private List<SysRoleMenu> sysRoleMenuList;

    @Transient
    private String createTimeStart;

    @Transient
    private String createTimeEnd;

    @Mapper
    public interface SysRoleModelMapper extends BaseModelMapper<SysRoleDto, SysRole> {
        /**
         * 转换Dto对象到实体对象。
         *
         * @param sysRoleDto 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysRoleMenuList", expression = "java(mapToBean(sysRoleDto.getSysRoleMenuList(), com.orange.demo.upmsservice.model.SysRoleMenu.class))")
        @Override
        SysRole toModel(SysRoleDto sysRoleDto);
        /**
         * 转换实体对象到Dto对象。
         *
         * @param sysRole 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysRoleMenuList", expression = "java(beanToMap(sysRole.getSysRoleMenuList(), false))")
        @Override
        SysRoleDto fromModel(SysRole sysRole);
    }
    public static final SysRoleModelMapper INSTANCE = Mappers.getMapper(SysRole.SysRoleModelMapper.class);
}
