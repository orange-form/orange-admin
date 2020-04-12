package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户与角色关联关系数据访问操作接口。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public interface SysUserRoleMapper extends BaseDaoMapper<SysUserRole> {

    /**
     * 获取用户的角色Id列表。
     *
     * @param userId 用户Id。
     * @return 角色Id列表。
     */
    List<Long> getRoleIdListByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色信息，如果用户角色已经存在，则不会重复插入。
     *
     * @param userRoleList 待插入的角色用户列表。
     */
    void addUserRoleList(List<SysUserRole> userRoleList);
}
