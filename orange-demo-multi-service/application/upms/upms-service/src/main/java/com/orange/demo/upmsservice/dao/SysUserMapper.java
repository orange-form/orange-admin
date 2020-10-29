package com.orange.demo.upmsservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upmsservice.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 用户管理数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysUserMapper extends BaseDaoMapper<SysUser> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param sysUserFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<SysUser> getSysUserList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param sysUserFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getSysUserCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("sysUserFilter") SysUser sysUserFilter);

    /**
     * 根据角色Id，获取关联的用户Id列表。
     *
     * @param roleId        关联的角色Id。
     * @param sysUserFilter 用户过滤条件对象。
     * @param orderBy       order by从句的参数。
     * @return 和RoleId关联的用户列表。
     */
    List<SysUser> getSysUserListByRoleId(
            @Param("roleId") Long roleId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据角色Id，获取和当前角色Id没有建立多对多关联关系的用户Id列表。
     *
     * @param roleId        关联的角色Id。
     * @param sysUserFilter 用户过滤条件对象。
     * @param orderBy       order by从句的参数。
     * @return 和RoleId没有建立关联关系的用户列表。
     */
    List<SysUser> getNotInSysUserListByRoleId(
            @Param("roleId") Long roleId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);
}
