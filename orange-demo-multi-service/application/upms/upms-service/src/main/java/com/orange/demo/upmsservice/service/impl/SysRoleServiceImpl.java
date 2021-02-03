package com.orange.demo.upmsservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.upmsservice.dao.SysRoleMapper;
import com.orange.demo.upmsservice.dao.SysRoleMenuMapper;
import com.orange.demo.upmsservice.dao.SysUserRoleMapper;
import com.orange.demo.upmsservice.model.SysRole;
import com.orange.demo.upmsservice.model.SysRoleMenu;
import com.orange.demo.upmsservice.model.SysUserRole;
import com.orange.demo.upmsservice.service.SysMenuService;
import com.orange.demo.upmsservice.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色数据服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends BaseService<SysRole, Long> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysRole> mapper() {
        return sysRoleMapper;
    }

    /**
     * 保存新增的角色对象。
     *
     * @param role      新增的角色对象。
     * @param menuIdSet 菜单Id列表。
     * @return 新增后的角色对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysRole saveNew(SysRole role, Set<Long> menuIdSet) {
        role.setRoleId(idGenerator.nextLongId());
        role.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        MyModelUtil.fillCommonsForInsert(role);
        sysRoleMapper.insert(role);
        if (menuIdSet != null) {
            List<SysRoleMenu> roleMenuList = new LinkedList<>();
            for (Long menuId : menuIdSet) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(menuId);
                roleMenuList.add(roleMenu);
            }
            sysRoleMenuMapper.insertList(roleMenuList);
        }
        return role;
    }

    /**
     * 更新角色对象。
     *
     * @param role         更新的角色对象。
     * @param originalRole 原有的角色对象。
     * @param menuIdSet    菜单Id列表。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysRole role, SysRole originalRole, Set<Long> menuIdSet) {
        role.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        MyModelUtil.fillCommonsForUpdate(role, originalRole);
        if (sysRoleMapper.updateByPrimaryKey(role) != 1) {
            return false;
        }
        SysRoleMenu deletedRoleMenu = new SysRoleMenu();
        deletedRoleMenu.setRoleId(role.getRoleId());
        sysRoleMenuMapper.delete(deletedRoleMenu);
        if (menuIdSet != null) {
            List<SysRoleMenu> roleMenuList = new LinkedList<>();
            for (Long menuId : menuIdSet) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(menuId);
                roleMenuList.add(roleMenu);
            }
            sysRoleMenuMapper.insertList(roleMenuList);
        }
        return true;
    }

    /**
     * 删除指定角色。
     *
     * @param roleId 角色主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long roleId) {
        if (!this.removeById(roleId)) {
            return false;
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleId(roleId);
        sysRoleMenuMapper.delete(roleMenu);
        SysUserRole userRole = new SysUserRole();
        userRole.setRoleId(roleId);
        sysUserRoleMapper.delete(userRole);
        return true;
    }

    /**
     * 获取角色列表。
     *
     * @param filter  角色过滤对象。
     * @param orderBy 排序参数。
     * @return 角色列表。
     */
    @Override
    public List<SysRole> getSysRoleList(SysRole filter, String orderBy) {
        return sysRoleMapper.getSysRoleList(filter, orderBy);
    }

    /**
     * 批量新增用户角色关联。
     *
     * @param userRoleList 用户角色关系数据列表。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUserRoleList(List<SysUserRole> userRoleList) {
        sysUserRoleMapper.insertList(userRoleList);
    }

    /**
     * 移除指定用户和指定角色的关联关系。
     *
     * @param roleId 角色主键Id。
     * @param userId 用户主键Id。
     * @return 移除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeUserRole(Long roleId, Long userId) {
        SysUserRole userRole  = new SysUserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        return sysUserRoleMapper.delete(userRole) == 1;
    }

    /**
     * 验证角色对象关联的数据是否都合法。
     *
     * @param sysRole          当前操作的对象。
     * @param originalSysRole  原有对象。
     * @param menuIdListString 逗号分隔的menuId列表。
     * @return 验证结果。
     */
    @Override
    public CallResult verifyRelatedData(SysRole sysRole, SysRole originalSysRole, String menuIdListString) {
        JSONObject jsonObject = null;
        if (StringUtils.isNotBlank(menuIdListString)) {
            Set<Long> menuIdSet = Arrays.stream(
                    menuIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            if (!sysMenuService.existAllPrimaryKeys(menuIdSet)) {
                return CallResult.error("数据验证失败，存在不合法的菜单权限，请刷新后重试！");
            }
            jsonObject = new JSONObject();
            jsonObject.put("menuIdSet", menuIdSet);
        }
        return CallResult.ok(jsonObject);
    }

    /**
     * 查询角色的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param roleId 角色Id。
     * @param url    url过滤条件。
     * @return 包含从角色到权限资源的完整权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysPermListWithDetail(Long roleId, String url) {
        return sysRoleMapper.getSysPermListWithDetail(roleId, url);
    }

    /**
     * 查询角色的权限字列表。同时返回详细的分配路径。
     *
     * @param roleId   角色Id。
     * @param permCode 权限字名称过滤条件。
     * @return 包含从角色到权限字的权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysPermCodeListWithDetail(Long roleId, String permCode) {
        return sysRoleMapper.getSysPermCodeListWithDetail(roleId, permCode);
    }
}
