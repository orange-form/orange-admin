package com.orange.demo.upms.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upms.model.SysMenu;

import java.util.List;

/**
 * 菜单数据访问操作接口。
 *
 * @author Jerry
 * @date 2020-09-24
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
