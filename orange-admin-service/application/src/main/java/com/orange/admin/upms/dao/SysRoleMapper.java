package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色数据访问操作接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public interface SysRoleMapper extends BaseDaoMapper<SysRole> {

    /**
     * 获取对象列表，过滤条件中包含like和between条件。
     *
     * @param sysRoleFilter 过滤对象。
     * @param orderBy       排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<SysRole> getSysRoleList(@Param("sysRoleFilter") SysRole sysRoleFilter, @Param("orderBy") String orderBy);

    /**
     * 根据权限字Id获取关联的角色列表。
     *
     * @param permCodeId 权限字Id。
     * @return 关联的角色列表。
     */
    List<SysRole> getSysRoleListByPermCodeId(@Param("permCodeId") Long permCodeId);

    /**
     * 根据url模糊查询关联的角色列表。
     *
     * @param url url片段。
     * @return 关联的角色列表。
     */
    List<SysRole> getSysRoleListByPerm(@Param("url") String url);
}
