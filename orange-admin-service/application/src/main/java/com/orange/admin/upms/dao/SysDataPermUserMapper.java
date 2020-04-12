package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysDataPermUser;

import java.util.List;

/**
 * 数据权限与用户关系数据访问操作接口。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public interface SysDataPermUserMapper extends BaseDaoMapper<SysDataPermUser> {

    /**
     * 获取用户的数据权限Id列表。
     *
     * @param userId 用户Id。
     * @return 数据权限Id列表。
     */
    List<Long> getDataPermIdListByUserId(Long userId);

    /**
     * 批量添加数据权限和用户关系的列表。
     *
     * @param dataPermUserList 数据权限用户关系列表。
     */
    void addDataPermUserList(List<SysDataPermUser> dataPermUserList);
}
