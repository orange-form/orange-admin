package com.orange.demo.upmsservice.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.orange.demo.upmsinterface.constant.SysUserType;
import com.orange.demo.upmsinterface.constant.SysUserStatus;
import com.orange.demo.common.core.annotation.RelationConstDict;
import com.orange.demo.common.core.annotation.RelationManyToMany;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.annotation.DeletedFlagColumn;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.upmsinterface.dto.SysUserDto;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * SysUser实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_sys_user")
public class SysUser {

    /**
     * 用户Id。
     */
    @NotNull(message = "数据验证失败，用户Id不能为空！")
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 登录用户名。
     */
    @NotBlank(message = "数据验证失败，登录用户名不能为空！")
    @Column(name = "login_name")
    private String loginName;

    /**
     * 用户密码。
     */
    @NotBlank(message = "数据验证失败，用户密码不能为空！")
    private String password;

    /**
     * 用户显示名称。
     */
    @NotBlank(message = "数据验证失败，用户显示名称不能为空！")
    @Column(name = "show_name")
    private String showName;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    @NotNull(message = "数据验证失败，用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)不能为空！")
    @ConstDictRef(constDictClass = SysUserType.class, message = "数据验证失败，用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)为无效值！")
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
    @NotNull(message = "数据验证失败，用户状态(0: 正常 1: 锁定)不能为空！")
    @ConstDictRef(constDictClass = SysUserStatus.class, message = "数据验证失败，用户状态(0: 正常 1: 锁定)为无效值！")
    @Column(name = "user_status")
    private Integer userStatus;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @JSONField(serialize = false)
    @DeletedFlagColumn
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    /**
     * 创建用户Id。
     */
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * 创建用户名。
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
    public interface SysUserModelMapper extends BaseModelMapper<SysUserDto, SysUser> {
        /**
         * 转换Dto对象到实体对象。
         *
         * @param sysUserDto 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysUserRoleList", expression = "java(mapToBean(sysUserDto.getSysUserRoleList(), com.orange.demo.upmsservice.model.SysUserRole.class))")
        @Override
        SysUser toModel(SysUserDto sysUserDto);
        /**
         * 转换实体对象到Dto对象。
         *
         * @param sysUser 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysUserRoleList", expression = "java(beanToMap(sysUser.getSysUserRoleList(), false))")
        @Override
        SysUserDto fromModel(SysUser sysUser);
    }
    public static final SysUserModelMapper INSTANCE = Mappers.getMapper(SysUserModelMapper.class);
}
