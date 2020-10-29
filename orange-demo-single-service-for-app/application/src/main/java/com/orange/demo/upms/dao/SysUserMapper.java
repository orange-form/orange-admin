package com.orange.demo.upms.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upms.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 用户管理数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface SysUserMapper extends BaseDaoMapper<SysUser> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param sysUserFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<SysUser> getSysUserList(
            @Param("sysUserFilter") SysUser sysUserFilter, @Param("orderBy") String orderBy);
}
