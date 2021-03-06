package com.orange.demo.webadmin.upms.model;

import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.annotation.RelationManyToMany;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.webadmin.upms.vo.SysPermCodeVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.persistence.*;
import java.util.*;

/**
 * 权限字实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_sys_perm_code")
public class SysPermCode {

    /**
     * 权限字Id。
     */
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
    @Column(name = "perm_code")
    private String permCode;

    /**
     * 权限类型(0: 表单 1: UI片段 2: 操作)。
     */
    @Column(name = "perm_code_type")
    private Integer permCodeType;

    /**
     * 显示名称。
     */
    @Column(name = "show_name")
    private String showName;

    /**
     * 显示顺序(数值越小，越靠前)。
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

    @RelationManyToMany(
            relationMapperName = "sysPermCodePermMapper",
            relationMasterIdField = "permCodeId",
            relationModelClass = SysPermCodePerm.class)
    @Transient
    private List<SysPermCodePerm> sysPermCodePermList;

    @Mapper
    public interface SysPermCodeModelMapper extends BaseModelMapper<SysPermCodeVo, SysPermCode> {
        /**
         * 转换VO对象到实体对象。
         *
         * @param sysPermCodeVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysPermCodePermList", expression = "java(mapToBean(sysPermCodeVo.getSysPermCodePermList(), com.orange.demo.webadmin.upms.model.SysPermCodePerm.class))")
        @Override
        SysPermCode toModel(SysPermCodeVo sysPermCodeVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param sysPermCode 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysPermCodePermList", expression = "java(beanToMap(sysPermCode.getSysPermCodePermList(), false))")
        @Override
        SysPermCodeVo fromModel(SysPermCode sysPermCode);
    }
    public static final SysPermCodeModelMapper INSTANCE = Mappers.getMapper(SysPermCode.SysPermCodeModelMapper.class);
}
