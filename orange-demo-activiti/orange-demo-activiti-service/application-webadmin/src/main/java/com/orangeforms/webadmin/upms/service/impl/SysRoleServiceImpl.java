package com.orangeforms.webadmin.upms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.alibaba.fastjson.JSONObject;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.webadmin.upms.dao.SysRoleMapper;
import com.orangeforms.webadmin.upms.dao.SysRoleMenuMapper;
import com.orangeforms.webadmin.upms.dao.SysUserRoleMapper;
import com.orangeforms.webadmin.upms.model.SysRole;
import com.orangeforms.webadmin.upms.model.SysRoleMenu;
import com.orangeforms.webadmin.upms.model.SysUserRole;
import com.orangeforms.webadmin.upms.service.SysMenuService;
import com.orangeforms.webadmin.upms.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2021-06-06
 */
@Slf4j
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
        MyModelUtil.fillCommonsForInsert(role);
        role.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        sysRoleMapper.insert(role);
        if (menuIdSet != null) {
            for (Long menuId : menuIdSet) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
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
        MyModelUtil.fillCommonsForUpdate(role, originalRole);
        if (sysRoleMapper.updateById(role) != 1) {
            return false;
        }
        SysRoleMenu deletedRoleMenu = new SysRoleMenu();
        deletedRoleMenu.setRoleId(role.getRoleId());
        sysRoleMenuMapper.delete(new QueryWrapper<>(deletedRoleMenu));
        if (menuIdSet != null) {
            for (Long menuId : menuIdSet) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
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
        if (sysRoleMapper.deleteById(roleId) != 1) {
            return false;
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleId(roleId);
        sysRoleMenuMapper.delete(new QueryWrapper<>(roleMenu));
        SysUserRole userRole = new SysUserRole();
        userRole.setRoleId(roleId);
        sysUserRoleMapper.delete(new QueryWrapper<>(userRole));
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

    @Override
    public List<SysUserRole> getSysUserRoleListByUserId(Long userId) {
        SysUserRole filter = new SysUserRole();
        filter.setUserId(userId);
        return sysUserRoleMapper.selectList(new QueryWrapper<>(filter));
    }

    /**
     * 批量新增用户角色关联。
     *
     * @param userRoleList 用户角色关系数据列表。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUserRoleList(List<SysUserRole> userRoleList) {
        for (SysUserRole userRole : userRoleList) {
            sysUserRoleMapper.insert(userRole);
        }
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
        return sysUserRoleMapper.delete(new QueryWrapper<>(userRole)) == 1;
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
