package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysDataPerm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据权限数据访问操作接口。
 * NOTE: 该对象一定不能被 @EnableDataPerm 注解标注，否则会导致无限递归。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public interface SysDataPermMapper extends BaseDaoMapper<SysDataPerm> {

    /**
     * 获取数据权限列表。
     *
     * @param sysDataPermFilter 过滤对象。
     * @param orderBy           排序字符串。
     * @return 过滤后的数据权限列表。
     */
    List<SysDataPerm> getSysDataPermList(
            @Param("sysDataPermFilter") SysDataPerm sysDataPermFilter, @Param("orderBy") String orderBy);
}
