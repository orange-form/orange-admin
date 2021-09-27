package com.flow.demo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.webadmin.upms.model.constant.SysUserType;
import com.flow.demo.webadmin.upms.model.constant.SysUserStatus;
import com.flow.demo.common.core.annotation.RelationDict;
import com.flow.demo.common.core.annotation.RelationConstDict;
import com.flow.demo.common.core.annotation.RelationManyToMany;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.webadmin.upms.vo.SysUserVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * SysUser实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_sys_user")
public class SysUser {

    /**
     * 用户Id。
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 登录用户名。
     */
    @TableField(value = "login_name")
    private String loginName;

    /**
     * 用户密码。
     */
    private String password;

    /**
     * 用户显示名称。
     */
    @TableField(value = "show_name")
    private String showName;

    /**
     * 用户部门Id。
     */
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    @TableField(value = "user_type")
    private Integer userType;

    /**
     * 用户头像的Url。
     */
    @TableField(value = "head_image_url")
    private String headImageUrl;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
    @TableField(value = "user_status")
    private Integer userStatus;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 更新者Id。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String createTimeEnd;

    /**
     * 多对多用户部门岗位数据集合。
     */
    @RelationManyToMany(
    relationMapperName = "sysUserPostMapper",
            relationMasterIdField = "userId",
            relationModelClass = SysUserPost.class)
    @TableField(exist = false)
    private List<SysUserPost> sysUserPostList;

    /**
     * 多对多用户角色数据集合。
     */
    @RelationManyToMany(
            relationMapperName = "sysUserRoleMapper",
            relationMasterIdField = "userId",
            relationModelClass = SysUserRole.class)
    @TableField(exist = false)
    private List<SysUserRole> sysUserRoleList;

    /**
     * 多对多用户数据权限数据集合。
     */
    @RelationManyToMany(
            relationMapperName = "sysDataPermUserMapper",
            relationMasterIdField = "userId",
            relationModelClass = SysDataPermUser.class)
    @TableField(exist = false)
    private List<SysDataPermUser> sysDataPermUserList;

    @RelationDict(
            masterIdField = "deptId",
            slaveServiceName = "sysDeptService",
            slaveModelClass = SysDept.class,
            slaveIdField = "deptId",
            slaveNameField = "deptName")
    @TableField(exist = false)
    private Map<String, Object> deptIdDictMap;

    @RelationConstDict(
            masterIdField = "userType",
            constantDictClass = SysUserType.class)
    @TableField(exist = false)
    private Map<String, Object> userTypeDictMap;

    @RelationConstDict(
            masterIdField = "userStatus",
            constantDictClass = SysUserStatus.class)
    @TableField(exist = false)
    private Map<String, Object> userStatusDictMap;

    @Mapper
    public interface SysUserModelMapper extends BaseModelMapper<SysUserVo, SysUser> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param sysUserVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "sysUserRoleList", expression = "java(mapToBean(sysUserVo.getSysUserRoleList(), com.flow.demo.webadmin.upms.model.SysUserRole.class))")
        @Mapping(target = "sysUserPostList", expression = "java(mapToBean(sysUserVo.getSysUserPostList(), com.flow.demo.webadmin.upms.model.SysUserPost.class))")
        @Mapping(target = "sysDataPermUserList", expression = "java(mapToBean(sysUserVo.getSysDataPermUserList(), com.flow.demo.webadmin.upms.model.SysDataPermUser.class))")
        @Override
        SysUser toModel(SysUserVo sysUserVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param sysUser 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "sysUserRoleList", expression = "java(beanToMap(sysUser.getSysUserRoleList(), false))")
        @Mapping(target = "sysUserPostList", expression = "java(beanToMap(sysUser.getSysUserPostList(), false))")
        @Mapping(target = "sysDataPermUserList", expression = "java(beanToMap(sysUser.getSysDataPermUserList(), false))")
        @Override
        SysUserVo fromModel(SysUser sysUser);
    }
    public static final SysUserModelMapper INSTANCE = Mappers.getMapper(SysUserModelMapper.class);
}
