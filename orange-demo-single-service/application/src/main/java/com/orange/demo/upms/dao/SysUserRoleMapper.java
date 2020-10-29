package com.orange.demo.upms.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upms.model.SysUserRole;

import java.util.List;

/**
 * 用户与角色关联关系数据访问操作接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface SysUserRoleMapper extends BaseDaoMapper<SysUserRole> {

    /**
     * 批量插入用户角色信息，如果用户角色已经存在，则不会重复插入。
     *
     * @param userRoleList 待插入的角色用户列表。
     */
    void addUserRoleList(List<SysUserRole> userRoleList);
}
