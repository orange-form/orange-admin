package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 用户管理数据操作访问接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public interface SysUserMapper extends BaseDaoMapper<SysUser> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param sysUserFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<SysUser> getSysUserList(
            @Param("sysUserFilter") SysUser sysUserFilter, @Param("orderBy") String orderBy);

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
     * @param orderBy order by从句的参数。
     * @return 和RoleId没有建立关联关系的用户列表。
     */
    List<SysUser> getNotInSysUserListByRoleId(
            @Param("roleId") Long roleId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据数据权限Id，获取关联的用户Id列表。
     *
     * @param dataPermId    关联的数据权限Id。
     * @param sysUserFilter 用户过滤条件对象。
     * @param orderBy order by从句的参数。
     * @return 和DataPermId关联的用户列表。
     */
    List<SysUser> getSysUserListByDataPermId(
            @Param("dataPermId") Long dataPermId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据数据权限Id，获取和当前数据权限Id没有建立多对多关联关系的用户Id列表。
     *
     * @param dataPermId    关联的数据权限Id。
     * @param sysUserFilter 用户过滤条件对象。
     * @param orderBy order by从句的参数。
     * @return 和DataPermId没有建立关联关系的用户列表。
     */
    List<SysUser> getNotInSysUserListByDataPermId(
            @Param("dataPermId") Long dataPermId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);
}
