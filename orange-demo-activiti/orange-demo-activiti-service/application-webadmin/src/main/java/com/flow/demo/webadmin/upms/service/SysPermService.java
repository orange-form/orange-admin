package com.flow.demo.webadmin.upms.service;

import com.flow.demo.common.core.base.service.IBaseService;
import com.flow.demo.common.core.object.CallResult;
import com.flow.demo.webadmin.upms.model.SysPerm;

import java.util.*;

/**
 * 权限资源数据服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysPermService extends IBaseService<SysPerm, Long> {

    /**
     * 保存新增的权限资源对象。
     *
     * @param perm 新增的权限资源对象。
     * @return 新增后的权限资源对象。
     */
    SysPerm saveNew(SysPerm perm);

    /**
     * 更新权限资源对象。
     *
     * @param perm         更新的权限资源对象。
     * @param originalPerm 原有的权限资源对象。
     * @return 更新成功返回true，否则false。
     */
    boolean update(SysPerm perm, SysPerm originalPerm);

    /**
     * 删除权限资源。
     *
     * @param permId 权限资源主键Id。
     * @return 删除成功返回true，否则false。
     */
    boolean remove(Long permId);

    /**
     * 获取权限数据列表。
     *
     * @param sysPermFilter 过滤对象。
     * @return 权限列表。
     */
    List<SysPerm> getPermListWithRelation(SysPerm sysPermFilter);

    /**
     * 将指定用户的指定会话的权限集合存入缓存。
     *
     * @param sessionId 会话Id。
     * @param userId    用户主键Id。
     * @return 查询并缓存后的权限集合。
     */
    Collection<String> putUserSysPermCache(String sessionId, Long userId);

    /**
     * 把在线表单的权限URL集合，存放到权限URL的缓存中。
     *
     * @param sessionId  会话Id。
     * @param permUrlSet URL集合。
     */
    void putOnlinePermToCache(String sessionId, Set<String> permUrlSet);

    /**
     * 将指定会话的权限集合从缓存中移除。
     *
     * @param sessionId 会话Id。
     */
    void removeUserSysPermCache(String sessionId);

    /**
     * 获取与指定用户关联的权限资源列表，已去重。
     *
     * @param userId 关联的用户主键Id。
     * @return 与指定用户Id关联的权限资源列表。
     */
    Collection<String> getPermListByUserId(Long userId);

    /**
     * 验证权限资源对象关联的数据是否都合法。
     *
     * @param sysPerm         当前操作的对象。
     * @param originalSysPerm 原有对象。
     * @return 验证结果。
     */
    CallResult verifyRelatedData(SysPerm sysPerm, SysPerm originalSysPerm);

    /**
     * 查询权限资源地址的用户列表。同时返回详细的分配路径。
     *
     * @param permId    权限资源Id。
     * @param loginName 登录名。
     * @return 包含从权限资源到用户的完整权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysUserListWithDetail(Long permId, String loginName);

    /**
     * 查询权限资源地址的角色列表。同时返回详细的分配路径。
     *
     * @param permId   权限资源Id。
     * @param roleName 角色名。
     * @return 包含从权限资源到角色的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysRoleListWithDetail(Long permId, String roleName);

    /**
     * 查询权限资源地址的菜单列表。同时返回详细的分配路径。
     *
     * @param permId   权限资源Id。
     * @param menuName 菜单名。
     * @return 包含从权限资源到菜单的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysMenuListWithDetail(Long permId, String menuName);
}
