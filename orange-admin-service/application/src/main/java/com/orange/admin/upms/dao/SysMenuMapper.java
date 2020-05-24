package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysMenu;

import java.util.List;

/**
 * 菜单数据访问操作接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public interface SysMenuMapper extends BaseDaoMapper<SysMenu> {

    /**
     * 获取登录用户的菜单列表。
     *
     * @param userId 登录用户。
     * @return 菜单列表。
     */
    List<SysMenu> getMenuListByUserId(Long userId);
}
