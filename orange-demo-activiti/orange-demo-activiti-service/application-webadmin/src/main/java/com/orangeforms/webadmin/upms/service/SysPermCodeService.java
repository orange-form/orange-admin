package com.orangeforms.webadmin.upms.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.webadmin.upms.model.SysPermCode;

import java.util.*;

/**
 * 权限字数据服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysPermCodeService extends IBaseService<SysPermCode, Long> {

    /**
     * 获取指定用户的权限字列表，已去重。
     *
     * @param userId 用户主键Id。
     * @return 用户关联的权限字列表。
     */
    Collection<String> getPermCodeListByUserId(Long userId);

    /**
     * 获取所有权限字数据列表，已去重。
     *
     * @return 全部权限字列表。
     */
    Collection<String> getAllPermCodeList();

    /**
     * 保存新增的权限字对象。
     *
     * @param sysPermCode 新增的权限字对象。
     * @param permIdSet   权限资源Id列表。
     * @return 新增后的权限字对象。
     */
    SysPermCode saveNew(SysPermCode sysPermCode, Set<Long> permIdSet);

    /**
     * 更新权限字对象。
     *
     * @param sysPermCode         更新的权限字对象。
     * @param originalSysPermCode 原有的权限字对象。
     * @param permIdSet           权限资源Id列表。
     * @return 更新成功返回true，否则false。
     */
    boolean update(SysPermCode sysPermCode, SysPermCode originalSysPermCode, Set<Long> permIdSet);

    /**
     * 删除指定的权限字。
     *
     * @param permCodeId 权限字主键Id。
     * @return 删除成功返回true，否则false。
     */
    boolean remove(Long permCodeId);

    /**
     * 判断当前权限字是否存在下级权限字对象。
     *
     * @param permCodeId 权限字主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasChildren(Long permCodeId);

    /**
     * 验证权限字对象关联的数据是否都合法。
     *
     * @param sysPermCode         当前操作的对象。
     * @param originalSysPermCode 原有对象。
     * @param permIdListString    逗号分隔的权限资源Id列表。
     * @return 验证结果。
     */
    CallResult verifyRelatedData(SysPermCode sysPermCode, SysPermCode originalSysPermCode, String permIdListString);

    /**
     * 查询权限字的用户列表。同时返回详细的分配路径。
     *
     * @param permCodeId 权限字Id。
     * @param loginName  登录名。
     * @return 包含从权限字到用户的完整权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysUserListWithDetail(Long permCodeId, String loginName);

    /**
     * 查询权限字的角色列表。同时返回详细的分配路径。
     *
     * @param permCodeId 权限字Id。
     * @param roleName   角色名。
     * @return 包含从权限字到角色的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysRoleListWithDetail(Long permCodeId, String roleName);
}
