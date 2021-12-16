package com.orangeforms.upmsservice.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.upmsservice.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 部门管理数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysDeptMapper extends BaseDaoMapper<SysDept> {

    /**
     * 批量插入对象列表。
     *
     * @param sysDeptList 新增对象列表。
     */
    void insertList(List<SysDept> sysDeptList);

    /**
     * 获取过滤后的对象列表。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param sysDeptFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<SysDept> getSysDeptList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("sysDeptFilter") SysDept sysDeptFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param sysDeptFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getSysDeptCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("sysDeptFilter") SysDept sysDeptFilter);
}
