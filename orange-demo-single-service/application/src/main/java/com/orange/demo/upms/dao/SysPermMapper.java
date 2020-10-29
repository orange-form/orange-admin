package com.orange.demo.upms.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upms.model.SysPerm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 权限资源数据访问操作接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface SysPermMapper extends BaseDaoMapper<SysPerm> {

    /**
     * 获取用户的权限列表。
     *
     * @param userId 用户Id。
     * @return 该用户的权限标识列表。
     */
    List<SysPerm> getPermListByUserId(@Param("userId") Long userId);

    /**
     * 获取指定用户Id的权限列表。
     *
     * @param loginName 精确匹配用户登录名。
     * @param moduleId  精确匹配权限模块Id。
     * @param url       权限的url过滤条件，LIKE %url%。
     * @return 权限列表。
     */
    List<Map<String, Object>> getUserPermListByFilter(
            @Param("loginName") String loginName, @Param("moduleId") Long moduleId, @Param("url") String url);

    /**
     * 根据关联权限字主键Id，获取权限资源数据列表。
     *
     * @param permCodeId 关联权限字主键Id。
     * @param orderBy    排序字符串，ORDER BY从句的参数。
     * @return 从表数据列表。
     */
    List<SysPerm> getPermListByPermCodeId(@Param("permCodeId") Long permCodeId, @Param("orderBy") String orderBy);

    /**
     * 获取指定权限的用户列表。
     *
     * @param permId 指定权限。
     * @return 用户列表。
     */
    List<Map<String, Object>> getPermUserListById(@Param("permId") Long permId);

    /**
     * 获取指定权限的角色列表。
     *
     * @param permId 指定权限。
     * @return 角色列表。
     */
    List<Map<String, Object>> getPermRoleListById(@Param("permId") Long permId);
}
