package com.orangeforms.webadmin.upms.service;

import com.orangeforms.webadmin.upms.model.*;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 用户管理数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysUserService extends IBaseService<SysUser, Long> {

    /**
     * 获取指定登录名的用户对象。
     *
     * @param loginName 指定登录用户名。
     * @return 用户对象。
     */
    SysUser getSysUserByLoginName(String loginName);

    /**
     * 保存新增的用户对象。
     *
     * @param user          新增的用户对象。
     * @param roleIdSet     用户角色Id集合。
     * @param deptPostIdSet 部门岗位Id集合。
     * @param dataPermIdSet 数据权限Id集合。
     * @return 新增后的用户对象。
     */
    SysUser saveNew(SysUser user, Set<Long> roleIdSet, Set<Long> deptPostIdSet, Set<Long> dataPermIdSet);

    /**
     * 更新用户对象。
     *
     * @param user          更新的用户对象。
     * @param originalUser  原有的用户对象。
     * @param roleIdSet     用户角色Id列表。
     * @param deptPostIdSet 部门岗位Id集合。
     * @param dataPermIdSet 数据权限Id集合。
     * @return 更新成功返回true，否则false。
     */
    boolean update(SysUser user, SysUser originalUser, Set<Long> roleIdSet, Set<Long> deptPostIdSet, Set<Long> dataPermIdSet);

    /**
     * 修改用户密码。
     * @param userId  用户主键Id。
     * @param newPass 新密码。
     * @return 成功返回true，否则false。
     */
    boolean changePassword(Long userId, String newPass);

    /**
     * 删除指定数据。
     *
     * @param userId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long userId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysUserListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysUser> getSysUserList(SysUser filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getSysUserList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<SysUser> getSysUserListWithRelation(SysUser filter, String orderBy);

    /**
     * 获取指定角色的用户列表。
     *
     * @param roleId  角色主键Id。
     * @param filter  用户过滤对象。
     * @param orderBy 排序参数。
     * @return 用户列表。
     */
    List<SysUser> getSysUserListByRoleId(Long roleId, SysUser filter, String orderBy);

    /**
     * 获取不属于指定角色的用户列表。
     *
     * @param roleId  角色主键Id。
     * @param filter  用户过滤对象。
     * @param orderBy 排序参数。
     * @return 用户列表。
     */
    List<SysUser> getNotInSysUserListByRoleId(Long roleId, SysUser filter, String orderBy);

    /**
     * 获取指定数据权限的用户列表。
     *
     * @param dataPermId 数据权限主键Id。
     * @param filter     用户过滤对象。
     * @param orderBy    排序参数。
     * @return 用户列表。
     */
    List<SysUser> getSysUserListByDataPermId(Long dataPermId, SysUser filter, String orderBy);

    /**
     * 获取不属于指定数据权限的用户列表。
     *
     * @param dataPermId 数据权限主键Id。
     * @param filter     用户过滤对象。
     * @param orderBy    排序参数。
     * @return 用户列表。
     */
    List<SysUser> getNotInSysUserListByDataPermId(Long dataPermId, SysUser filter, String orderBy);

    /**
     * 获取指定部门岗位的用户列表。
     *
     * @param deptPostId 部门岗位主键Id。
     * @param filter     用户过滤对象。
     * @param orderBy    排序参数。
     * @return 用户列表。
     */
    List<SysUser> getSysUserListByDeptPostId(Long deptPostId, SysUser filter, String orderBy);

    /**
     * 获取不属于指定部门岗位的用户列表。
     *
     * @param deptPostId 部门岗位主键Id。
     * @param filter     用户过滤对象。
     * @param orderBy    排序参数。
     * @return 用户列表。
     */
    List<SysUser> getNotInSysUserListByDeptPostId(Long deptPostId, SysUser filter, String orderBy);

    /**
     * 获取指定岗位的用户列表。
     *
     * @param postId  岗位主键Id。
     * @param filter  用户过滤对象。
     * @param orderBy 排序参数。
     * @return 用户列表。
     */
    List<SysUser> getSysUserListByPostId(Long postId, SysUser filter, String orderBy);

    /**
     * 查询用户的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param userId 用户Id。
     * @param url    url过滤条件。
     * @return 包含从用户到权限资源的完整权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysPermListWithDetail(Long userId, String url);

    /**
     * 查询用户的权限字列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param permCode 权限字名称过滤条件。
     * @return 包含从用户到权限字的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysPermCodeListWithDetail(Long userId, String permCode);

    /**
     * 查询用户的菜单列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param menuName 菜单名称过滤条件。
     * @return 包含从用户到菜单的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysMenuListWithDetail(Long userId, String menuName);

    /**
     * 验证用户对象关联的数据是否都合法。
     *
     * @param sysUser         当前操作的对象。
     * @param originalSysUser 原有对象。
     * @param roleIds         逗号分隔的角色Id列表字符串。
     * @param deptPostIds     逗号分隔的部门岗位Id列表字符串。
     * @param dataPermIds     逗号分隔的数据权限Id列表字符串。
     * @return 验证结果。
     */
    CallResult verifyRelatedData(
            SysUser sysUser, SysUser originalSysUser, String roleIds, String deptPostIds, String dataPermIds);
}
