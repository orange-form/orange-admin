package com.orangeforms.webadmin.upms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.webadmin.upms.dao.SysMenuMapper;
import com.orangeforms.webadmin.upms.dao.SysMenuPermCodeMapper;
import com.orangeforms.webadmin.upms.dao.SysRoleMenuMapper;
import com.orangeforms.webadmin.upms.model.SysMenu;
import com.orangeforms.webadmin.upms.model.SysMenuPermCode;
import com.orangeforms.webadmin.upms.model.SysRoleMenu;
import com.orangeforms.webadmin.upms.model.constant.SysMenuType;
import com.orangeforms.webadmin.upms.model.constant.SysOnlineMenuPermType;
import com.orangeforms.webadmin.upms.service.SysMenuService;
import com.orangeforms.webadmin.upms.service.SysPermCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单数据服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("sysMenuService")
public class SysMenuServiceImpl extends BaseService<SysMenu, Long> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysMenuPermCodeMapper sysMenuPermCodeMapper;
    @Autowired
    private SysPermCodeService sysPermCodeService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysMenu> mapper() {
        return sysMenuMapper;
    }

    /**
     * 保存新增的菜单对象。
     *
     * @param sysMenu       新增的菜单对象。
     * @param permCodeIdSet 权限字Id列表。
     * @return 新增后的菜单对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysMenu saveNew(SysMenu sysMenu, Set<Long> permCodeIdSet) {
        sysMenu.setMenuId(idGenerator.nextLongId());
        MyModelUtil.fillCommonsForInsert(sysMenu);
        sysMenu.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        sysMenuMapper.insert(sysMenu);
        if (permCodeIdSet != null) {
            for (Long permCodeId : permCodeIdSet) {
                SysMenuPermCode menuPermCode = new SysMenuPermCode();
                menuPermCode.setMenuId(sysMenu.getMenuId());
                menuPermCode.setPermCodeId(permCodeId);
                sysMenuPermCodeMapper.insert(menuPermCode);
            }
        }
        // 判断当前菜单是否为指向在线表单的菜单，并将根据约定，动态插入两个子菜单。
        if (sysMenu.getOnlineFormId() != null && sysMenu.getOnlineFlowEntryId() == null) {
            SysMenu viewSubMenu = new SysMenu();
            viewSubMenu.setMenuId(idGenerator.nextLongId());
            viewSubMenu.setParentId(sysMenu.getMenuId());
            viewSubMenu.setMenuType(SysMenuType.TYPE_BUTTON);
            viewSubMenu.setMenuName("查看");
            viewSubMenu.setShowOrder(0);
            viewSubMenu.setOnlineFormId(sysMenu.getOnlineFormId());
            viewSubMenu.setOnlineMenuPermType(SysOnlineMenuPermType.TYPE_VIEW);
            viewSubMenu.setDeletedFlag(GlobalDeletedFlag.NORMAL);
            MyModelUtil.fillCommonsForInsert(viewSubMenu);
            sysMenuMapper.insert(viewSubMenu);
            SysMenu editSubMenu = new SysMenu();
            editSubMenu.setMenuId(idGenerator.nextLongId());
            editSubMenu.setParentId(sysMenu.getMenuId());
            editSubMenu.setMenuType(SysMenuType.TYPE_BUTTON);
            editSubMenu.setMenuName("编辑");
            editSubMenu.setShowOrder(1);
            editSubMenu.setOnlineFormId(sysMenu.getOnlineFormId());
            editSubMenu.setOnlineMenuPermType(SysOnlineMenuPermType.TYPE_EDIT);
            editSubMenu.setDeletedFlag(GlobalDeletedFlag.NORMAL);
            MyModelUtil.fillCommonsForInsert(editSubMenu);
            sysMenuMapper.insert(editSubMenu);
        }
        return sysMenu;
    }

    /**
     * 更新菜单对象。
     *
     * @param sysMenu         更新的菜单对象。
     * @param originalSysMenu 原有的菜单对象。
     * @param permCodeIdSet   权限字Id列表。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysMenu sysMenu, SysMenu originalSysMenu, Set<Long> permCodeIdSet) {
        MyModelUtil.fillCommonsForUpdate(sysMenu, originalSysMenu);
        sysMenu.setMenuType(originalSysMenu.getMenuType());
        UpdateWrapper<SysMenu> uw = this.createUpdateQueryForNullValue(sysMenu, sysMenu.getMenuId());
        if (sysMenuMapper.update(sysMenu, uw) != 1) {
            return false;
        }
        SysMenuPermCode deletedMenuPermCode = new SysMenuPermCode();
        deletedMenuPermCode.setMenuId(sysMenu.getMenuId());
        sysMenuPermCodeMapper.delete(new QueryWrapper<>(deletedMenuPermCode));
        if (permCodeIdSet != null) {
            for (Long permCodeId : permCodeIdSet) {
                SysMenuPermCode menuPermCode = new SysMenuPermCode();
                menuPermCode.setMenuId(sysMenu.getMenuId());
                menuPermCode.setPermCodeId(permCodeId);
                sysMenuPermCodeMapper.insert(menuPermCode);
            }
        }
        // 如果当前菜单的在线表单Id变化了，就需要同步更新他的内置子菜单也同步更新。
        if (ObjectUtil.notEqual(originalSysMenu.getOnlineFormId(), sysMenu.getOnlineFormId())) {
            SysMenu onlineSubMenu = new SysMenu();
            onlineSubMenu.setOnlineFormId(sysMenu.getOnlineFormId());
            sysMenuMapper.update(onlineSubMenu,
                    new QueryWrapper<SysMenu>().lambda().eq(SysMenu::getParentId, sysMenu.getMenuId()));
        }
        return true;
    }

    /**
     * 删除指定的菜单。
     *
     * @param menu 菜单对象。
     * @return 删除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(SysMenu menu) {
        Long menuId = menu.getMenuId();
        if (sysMenuMapper.deleteById(menuId) != 1) {
            return false;
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setMenuId(menuId);
        sysRoleMenuMapper.delete(new QueryWrapper<>(roleMenu));
        SysMenuPermCode menuPermCode = new SysMenuPermCode();
        menuPermCode.setMenuId(menuId);
        sysMenuPermCodeMapper.delete(new QueryWrapper<>(menuPermCode));
        // 如果为指向在线表单的菜单，则连同删除子菜单
        if (menu.getOnlineFormId() != null) {
            sysMenuMapper.delete(new QueryWrapper<SysMenu>().lambda().eq(SysMenu::getParentId, menuId));
        }
        return true;
    }

    /**
     * 获取全部菜单列表。
     *
     * @return 全部菜单列表。
     */
    @Override
    public Collection<SysMenu> getAllMenuList() {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(this.safeMapToColumnName("showOrder"));
        queryWrapper.in(this.safeMapToColumnName("menuType"),
                Arrays.asList(SysMenuType.TYPE_MENU, SysMenuType.TYPE_DIRECTORY));
        return sysMenuMapper.selectList(queryWrapper);
    }

    /**
     * 获取指定用户Id的菜单列表，已去重。
     *
     * @param userId 用户主键Id。
     * @return 用户关联的菜单列表。
     */
    @Override
    public Collection<SysMenu> getMenuListByUserId(Long userId) {
        List<SysMenu> menuList = sysMenuMapper.getMenuListByUserId(userId);
        LinkedHashMap<Long, SysMenu> menuMap = new LinkedHashMap<>();
        for (SysMenu menu : menuList) {
            menuMap.put(menu.getMenuId(), menu);
        }
        return menuMap.values();
    }

    /**
     * 判断当前菜单是否存在子菜单。
     *
     * @param menuId 菜单主键Id。
     * @return 存在返回true，否则false。
     */
    @Override
    public boolean hasChildren(Long menuId) {
        SysMenu menu = new SysMenu();
        menu.setParentId(menuId);
        return this.getCountByFilter(menu) > 0;
    }

    /**
     * 验证菜单对象关联的数据是否都合法。
     *
     * @param sysMenu              当前操作的对象。
     * @param originalSysMenu      原有对象。
     * @param permCodeIdListString 逗号分隔的权限Id列表。
     * @return 验证结果。
     */
    @Override
    public CallResult verifyRelatedData(SysMenu sysMenu, SysMenu originalSysMenu, String permCodeIdListString) {
        // menu、ui fragment和button类型的menu不能没有parentId
        if (sysMenu.getParentId() == null) {
            if (sysMenu.getMenuType() != SysMenuType.TYPE_DIRECTORY) {
                return CallResult.error("数据验证失败，当前类型菜单项的上级菜单不能为空！");
            }
        }
        if (this.needToVerify(sysMenu, originalSysMenu, SysMenu::getParentId)) {
            String errorMessage = checkErrorOfNonDirectoryMenu(sysMenu);
            if (errorMessage != null) {
                return CallResult.error(errorMessage);
            }
        }
        JSONObject jsonObject = null;
        if (StringUtils.isNotBlank(permCodeIdListString)) {
            Set<Long> permCodeIdSet = Arrays.stream(
                    permCodeIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            if (!sysPermCodeService.existAllPrimaryKeys(permCodeIdSet)) {
                return CallResult.error("数据验证失败，存在不合法的权限字，请刷新后重试！");
            }
            jsonObject = new JSONObject();
            jsonObject.put("permCodeIdSet", permCodeIdSet);
        }
        return CallResult.ok(jsonObject);
    }

    /**
     * 查询菜单的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param menuId 菜单Id。
     * @param url    权限资源地址过滤条件。
     * @return 包含从菜单到权限资源的权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysPermListWithDetail(Long menuId, String url) {
        return sysMenuMapper.getSysPermListWithDetail(menuId, url);
    }

    /**
     * 查询菜单的用户列表。同时返回详细的分配路径。
     *
     * @param menuId    菜单Id。
     * @param loginName 登录名。
     * @return 包含从菜单到用户的完整权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysUserListWithDetail(Long menuId, String loginName) {
        return sysMenuMapper.getSysUserListWithDetail(menuId, loginName);
    }

    /**
     * 获取指定类型的所有在线表单的菜单。
     *
     * @param menuType 菜单类型，NULL则返回全部类型。
     * @return 在线表单关联的菜单列表。
     */
    @Override
    public List<SysMenu> getAllOnlineMenuList(Integer menuType) {
        LambdaQueryWrapper<SysMenu> queryWrapper =
                new QueryWrapper<SysMenu>().lambda().isNotNull(SysMenu::getOnlineFormId);
        if (menuType != null) {
            queryWrapper.eq(SysMenu::getMenuType, menuType);
        }
        return sysMenuMapper.selectList(queryWrapper);
    }

    /**
     * 获取当前用户有权访问的在线表单菜单，仅返回类型为BUTTON的菜单。
     *
     * @param userId   指定的用户。
     * @param menuType 菜单类型，NULL则返回全部类型。
     * @return 在线表单关联的菜单列表。
     */
    @Override
    public List<SysMenu> getOnlineMenuListByUserId(Long userId, Integer menuType) {
        return sysMenuMapper.getOnlineMenuListByUserId(userId, menuType);
    }

    private String checkErrorOfNonDirectoryMenu(SysMenu sysMenu) {
        // 判断父节点是否存在
        SysMenu parentSysMenu = getById(sysMenu.getParentId());
        if (parentSysMenu == null) {
            return "数据验证失败，关联的上级菜单并不存在，请刷新后重试！";
        }
        // 逐个判断每种类型的菜单，他的父菜单的合法性，先从目录类型和菜单类型开始
        if (sysMenu.getMenuType() == SysMenuType.TYPE_DIRECTORY
                || sysMenu.getMenuType() == SysMenuType.TYPE_MENU) {
            // 他们的上级只能是目录
            if (parentSysMenu.getMenuType() != SysMenuType.TYPE_DIRECTORY) {
                return "数据验证失败，当前类型菜单项的上级菜单只能是目录类型！";
            }
        } else if (sysMenu.getMenuType() == SysMenuType.TYPE_UI_FRAGMENT) {
            // ui fragment的上级只能是menu类型
            if (parentSysMenu.getMenuType() != SysMenuType.TYPE_MENU) {
                return "数据验证失败，当前类型菜单项的上级菜单只能是菜单类型和按钮类型！";
            }
        } else if (sysMenu.getMenuType() == SysMenuType.TYPE_BUTTON) {
            // button的上级只能是menu和ui fragment
            if (parentSysMenu.getMenuType() != SysMenuType.TYPE_MENU
                    && parentSysMenu.getMenuType() != SysMenuType.TYPE_UI_FRAGMENT) {
                return "数据验证失败，当前类型菜单项的上级菜单只能是菜单类型和UI片段类型！";
            }
        }
        return null;
    }
}
