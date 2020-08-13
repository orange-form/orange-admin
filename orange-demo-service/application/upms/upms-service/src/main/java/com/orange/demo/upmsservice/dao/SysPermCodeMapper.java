package com.orange.demo.upmsservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upmsservice.model.SysPermCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 权限字数据访问操作接口。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
public interface SysPermCodeMapper extends BaseDaoMapper<SysPermCode> {

    /**
     * 获取用户的所有权限字列表。
     *
     * @param userId 用户Id。
     * @return 该用户的权限字列表。
     */
    List<String> getPermCodeListByUserId(Long userId);

    /**
     * 获取该菜单的权限字和关联的权限资源列表。
     *
     * @param menuId 菜单Id。
     * @return 权限字和关联的权限资源列表。
     */
    List<Map<String, Object>> getPermCodeListByMenuId(Long menuId);

    /**
     * 获取指定用户的权限字列表。
     *
     * @param loginName 精确匹配用户登录名。
     * @param permCode  模糊匹配的权限字名，LIKE %permCode%。
     * @return 权限字列表。
     */
    List<SysPermCode> getUserPermCodeListByFilter(
            @Param("loginName") String loginName, @Param("permCode") String permCode);
}
