package com.orangeforms.webadmin.upms.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.webadmin.upms.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 用户管理数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysUserMapper extends BaseDaoMapper<SysUser> {

    /**
     * 批量插入对象列表。
     *
     * @param sysUserList 新增对象列表。
     */
    void insertList(List<SysUser> sysUserList);

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

    /**
     * 根据部门岗位Id，获取关联的用户Id列表。
     *
     * @param deptPostId    关联的部门岗位Id。
     * @param sysUserFilter 用户过滤条件对象。
     * @param orderBy       order by从句的参数。
     * @return 和DeptPostId关联的用户列表。
     */
    List<SysUser> getSysUserListByDeptPostId(
            @Param("deptPostId") Long deptPostId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据部门岗位Id，获取和当前部门岗位Id没有建立多对多关联关系的用户Id列表。
     *
     * @param deptPostId    关联的部门岗位Id。
     * @param sysUserFilter 用户过滤条件对象。
     * @param orderBy       order by从句的参数。
     * @return 和deptPostId没有建立关联关系的用户列表。
     */
    List<SysUser> getNotInSysUserListByDeptPostId(
            @Param("deptPostId") Long deptPostId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据岗位Id，获取关联的用户Id列表。
     *
     * @param postId        关联的岗位Id。
     * @param sysUserFilter 用户过滤条件对象。
     * @param orderBy       order by从句的参数。
     * @return 和postId关联的用户列表。
     */
    List<SysUser> getSysUserListByPostId(
            @Param("postId") Long postId,
            @Param("sysUserFilter") SysUser sysUserFilter,
            @Param("orderBy") String orderBy);

    /**
     * 查询用户的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param userId 用户Id。
     * @param url    url过滤条件。
     * @return 包含从用户到权限资源的完整权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysPermListWithDetail(
            @Param("userId") Long userId, @Param("url") String url);

    /**
     * 查询用户的权限字列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param permCode 权限字名称过滤条件。
     * @return 包含从用户到权限字的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysPermCodeListWithDetail(
            @Param("userId") Long userId, @Param("permCode") String permCode);

    /**
     * 查询用户的菜单列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param menuName 菜单名称过滤条件。
     * @return 包含从用户到菜单的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysMenuListWithDetail(
            @Param("userId") Long userId, @Param("menuName") String menuName);
}
