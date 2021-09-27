package com.flow.demo.webadmin.upms.dao;

import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.webadmin.upms.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 角色数据访问操作接口。
 *
 * @author Jerry
 * @date 2021-06-06
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
     * 查询角色的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param roleId 角色Id。
     * @param url    url过滤条件。
     * @return 包含从角色到权限资源的完整权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysPermListWithDetail(
            @Param("roleId") Long roleId, @Param("url") String url);

    /**
     * 查询角色的权限字列表。同时返回详细的分配路径。
     *
     * @param roleId   角色Id。
     * @param permCode 权限字名称过滤条件。
     * @return 包含从角色到权限字的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysPermCodeListWithDetail(
            @Param("roleId") Long roleId, @Param("permCode") String permCode);
}
