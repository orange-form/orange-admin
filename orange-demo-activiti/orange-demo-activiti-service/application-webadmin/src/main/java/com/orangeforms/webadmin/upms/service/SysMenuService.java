package com.orangeforms.webadmin.upms.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.webadmin.upms.model.SysMenu;

import java.util.*;

/**
 * 菜单数据服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysMenuService extends IBaseService<SysMenu, Long> {

    /**
     * 保存新增的菜单对象。
     *
     * @param sysMenu       新增的菜单对象。
     * @param permCodeIdSet 权限字Id列表。
     * @return 新增后的菜单对象。
     */
    SysMenu saveNew(SysMenu sysMenu, Set<Long> permCodeIdSet);

    /**
     * 更新菜单对象。
     *
     * @param sysMenu         更新的菜单对象。
     * @param originalSysMenu 原有的菜单对象。
     * @param permCodeIdSet   权限字Id列表。
     * @return 更新成功返回true，否则false。
     */
    boolean update(SysMenu sysMenu, SysMenu originalSysMenu, Set<Long> permCodeIdSet);

    /**
     * 删除指定的菜单。
     *
     * @param menu 菜单对象。
     * @return 删除成功返回true，否则false。
     */
    boolean remove(SysMenu menu);

    /**
     * 获取全部菜单列表。
     *
     * @return 全部菜单列表。
     */
    Collection<SysMenu> getAllMenuList();

    /**
     * 获取指定用户Id的菜单列表，已去重。
     *
     * @param userId 用户主键Id。
     * @return 用户关联的菜单列表。
     */
    Collection<SysMenu> getMenuListByUserId(Long userId);

    /**
     * 判断当前菜单是否存在子菜单。
     *
     * @param menuId 菜单主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasChildren(Long menuId);

    /**
     * 验证菜单对象关联的数据是否都合法。
     *
     * @param sysMenu              当前操作的对象。
     * @param originalSysMenu      原有对象。
     * @param permCodeIdListString 逗号分隔的权限Id列表。
     * @return 验证结果。
     */
    CallResult verifyRelatedData(SysMenu sysMenu, SysMenu originalSysMenu, String permCodeIdListString);

    /**
     * 查询菜单的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param menuId 菜单Id。
     * @param url    权限资源地址过滤条件。
     * @return 包含从菜单到权限资源的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysPermListWithDetail(Long menuId, String url);

    /**
     * 查询菜单的用户列表。同时返回详细的分配路径。
     *
     * @param menuId    菜单Id。
     * @param loginName 登录名。
     * @return 包含从菜单到用户的完整权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysUserListWithDetail(Long menuId, String loginName);

    /**
     * 获取指定类型的所有在线表单的菜单。
     *
     * @param menuType 菜单类型，NULL则返回全部类型。
     * @return 在线表单关联的菜单列表。
     */
    List<SysMenu> getAllOnlineMenuList(Integer menuType);

    /**
     * 获取当前用户有权访问的在线表单菜单，仅返回类型为BUTTON的菜单。
     *
     * @param userId   指定的用户。
     * @param menuType 菜单类型，NULL则返回全部类型。
     * @return 在线表单关联的菜单列表。
     */
    List<SysMenu> getOnlineMenuListByUserId(Long userId, Integer menuType);
}
