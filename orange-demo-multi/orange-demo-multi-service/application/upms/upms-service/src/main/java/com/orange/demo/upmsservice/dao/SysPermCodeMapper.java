package com.orange.demo.upmsservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upmsservice.model.SysPermCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 权限字数据访问操作接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysPermCodeMapper extends BaseDaoMapper<SysPermCode> {

    /**
     * 获取用户的所有权限字列表。
     *
     * @param userId 用户Id。
     * @return 该用户的权限字列表。
     */
    List<String> getPermCodeListByUserId(@Param("userId") Long userId);

    /**
     * 查询权限字的用户列表。同时返回详细的分配路径。
     *
     * @param permCodeId 权限字Id。
     * @param loginName  登录名。
     * @return 包含从权限字到用户的完整权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysUserListWithDetail(
            @Param("permCodeId") Long permCodeId, @Param("loginName") String loginName);

    /**
     * 查询权限字的角色列表。同时返回详细的分配路径。
     *
     * @param permCodeId 权限字Id。
     * @param roleName   角色名。
     * @return 包含从权限字到角色的权限分配路径信息的查询结果列表。
     */
    List<Map<String, Object>> getSysRoleListWithDetail(
            @Param("permCodeId") Long permCodeId, @Param("roleName") String roleName);
}
