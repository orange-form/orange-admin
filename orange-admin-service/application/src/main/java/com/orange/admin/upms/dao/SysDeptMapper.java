package com.orange.admin.upms.dao;

import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 部门管理数据操作访问接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
public interface SysDeptMapper extends BaseDaoMapper<SysDept> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param sysDeptFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<SysDept> getSysDeptList(
            @Param("sysDeptFilter") SysDept sysDeptFilter, @Param("orderBy") String orderBy);
}
