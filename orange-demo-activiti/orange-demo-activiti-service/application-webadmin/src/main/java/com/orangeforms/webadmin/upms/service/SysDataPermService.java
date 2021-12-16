package com.orangeforms.webadmin.upms.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.webadmin.upms.model.*;

import java.util.*;

/**
 * 数据权限数据服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysDataPermService extends IBaseService<SysDataPerm, Long> {

    /**
     * 保存新增的数据权限对象。
     *
     * @param dataPerm         新增的数据权限对象。
     * @param deptIdSet        关联的部门Id列表。
     * @return 新增后的数据权限对象。
     */
    SysDataPerm saveNew(SysDataPerm dataPerm, Set<Long> deptIdSet);

    /**
     * 更新数据权限对象。
     *
     * @param dataPerm         更新的数据权限对象。
     * @param originalDataPerm 原有的数据权限对象。
     * @param deptIdSet        关联的部门Id列表。
     * @return 更新成功返回true，否则false。
     */
    boolean update(SysDataPerm dataPerm, SysDataPerm originalDataPerm, Set<Long> deptIdSet);

    /**
     * 删除指定数据权限。
     *
     * @param dataPermId 数据权限主键Id。
     * @return 删除成功返回true，否则false。
     */
    boolean remove(Long dataPermId);

    /**
     * 获取数据权限列表。
     *
     * @param filter  数据权限过滤对象。
     * @param orderBy 排序参数。
     * @return 数据权限查询列表。
     */
    List<SysDataPerm> getSysDataPermList(SysDataPerm filter, String orderBy);

    /**
     * 将指定用户的指定会话的数据权限集合存入缓存。
     *
     * @param sessionId 会话Id。
     * @param userId    用户主键Id。
     * @param deptId    用户所属部门主键Id。
     * @return 查询并缓存后的数据权限集合。返回格式为，Map<RuleType, DeptIdString>。
     */
    Map<Integer, String> putDataPermCache(String sessionId, Long userId, Long deptId);

    /**
     * 将指定会话的数据权限集合从缓存中移除。
     *
     * @param sessionId 会话Id。
     */
    void removeDataPermCache(String sessionId);

    /**
     * 获取指定用户Id的数据权限列表。并基于权限规则类型进行了一级分组。
     *
     * @param userId 指定的用户Id。
     * @param deptId 用户所属部门主键Id。
     * @return 合并优化后的数据权限列表。返回格式为，Map<RuleType, DeptIdString>。
     */
    Map<Integer, String> getSysDataPermListByUserId(Long userId, Long deptId);

    /**
     * 添加用户和数据权限之间的多对多关联关系。
     *
     * @param dataPermId 数据权限Id。
     * @param userIdSet  关联的用户Id列表。
     */
    void addDataPermUserList(Long dataPermId, Set<Long> userIdSet);

    /**
     * 移除用户和数据权限之间的多对多关联关系。
     *
     * @param dataPermId 数据权限主键Id。
     * @param userId     用户主键Id。
     * @return true移除成功，否则false。
     */
    boolean removeDataPermUser(Long dataPermId, Long userId);

    /**
     * 验证数据权限对象关联菜单数据是否都合法。
     *
     * @param dataPerm         数据权限关对象。
     * @param deptIdListString 与数据权限关联的部门Id列表。
     * @return 验证结果。
     */
    CallResult verifyRelatedData(SysDataPerm dataPerm, String deptIdListString);
}
