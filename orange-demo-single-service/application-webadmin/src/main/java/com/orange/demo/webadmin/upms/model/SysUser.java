package com.orange.demo.webadmin.upms.model;

import com.orange.demo.webadmin.upms.model.constant.SysUserType;
import com.orange.demo.webadmin.upms.model.constant.SysUserStatus;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.annotation.RelationManyToMany;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.webadmin.upms.vo.SysUserVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * SysUser实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_sys_user")
public class SysUser {

    /**
     * 用户Id。
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 登录用户名。
     */
    @Column(name = "login_name")
    private String loginName;

    /**
     * 用户密码。
     */
    private String password;

    /**
     * 用户显示名称。
     */
    @Column(name = "show_name")
    private String showName;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    @Column(name = "user_type")
    private Integer userType;

    /**
     * 用户头像的Url。
     */
    @Column(name = "head_image_url")
    private String headImageUrl;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
    @Column(name = "user_status")
    private Integer userStatus;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    /**
     * 创建用户Id。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 更新者Id。
     */
    @Column(name = "update_user_id")
    private Long updateUserId;

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
     * createTime 范围过滤起始值(>=)。
     */
    @Transient
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @Transient
    private String createTimeEnd;

    /**
     * 多对多用户角色数据集合。
     */
    @RelationManyToMany(
            relationMapperName = "sysUserRoleMapper",
            relationMasterIdField = "userId",
            relationModelClass = SysUserRole.class)
    @Transient
    private List<SysUserRole> sysUserRoleList;

    @RelationConstDict(
            masterIdField = "userType",
            constantDictClass = SysUserType.class)
    @Transient
    private Map<String, Object> userTypeDictMap;

    @RelationConstDict(
            masterIdField = "userStatus",
            constantDictClass = SysUserStatus.class)
    @Transient
    private Map<String, Object> userStatusDictMap;

    @Mapper
    public interface SysUserModelMapper extends BaseModelMapper<SysUserVo, SysUser> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param sysUserVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysUserRoleList", expression = "java(mapToBean(sysUserVo.getSysUserRoleList(), com.orange.demo.webadmin.upms.model.SysUserRole.class))")
        @Override
        SysUser toModel(SysUserVo sysUserVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param sysUser 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysUserRoleList", expression = "java(beanToMap(sysUser.getSysUserRoleList(), false))")
        @Override
        SysUserVo fromModel(SysUser sysUser);
    }
    public static final SysUserModelMapper INSTANCE = Mappers.getMapper(SysUserModelMapper.class);
}
