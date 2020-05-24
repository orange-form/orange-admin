package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysDataPermMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据权限与菜单关系数据访问操作接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public interface SysDataPermMenuMapper extends BaseDaoMapper<SysDataPermMenu> {

    /**
     * 获取指定用户Id的数据权限列表。
     *
     * @param userId 指定的用户Id。
     * @return 数据权限列表。
     */
    List<SysDataPermMenu> getSysDataPermMenuListByUserId(@Param("userId") Long userId);
}
