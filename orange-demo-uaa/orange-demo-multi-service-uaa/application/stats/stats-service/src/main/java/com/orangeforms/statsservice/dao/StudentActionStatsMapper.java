package com.orangeforms.statsservice.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.statsservice.model.StudentActionStats;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 学生行为统计数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface StudentActionStatsMapper extends BaseDaoMapper<StudentActionStats> {

    /**
     * 批量插入对象列表。
     *
     * @param studentActionStatsList 新增对象列表。
     */
    void insertList(List<StudentActionStats> studentActionStatsList);

    /**
     * 获取分组计算后的数据对象列表。
     *
     * @param studentActionStatsFilter 主表过滤对象。
     * @param groupSelect 分组显示字段列表字符串，SELECT从句的参数。
     * @param groupBy 分组字段列表字符串，GROUP BY从句的参数。
     * @param orderBy 排序字符串，ORDER BY从句的参数。
     * @return 对象列表。
     */
    List<StudentActionStats> getGroupedStudentActionStatsList(
            @Param("studentActionStatsFilter") StudentActionStats studentActionStatsFilter,
            @Param("groupSelect") String groupSelect,
            @Param("groupBy") String groupBy,
            @Param("orderBy") String orderBy);

    /**
     * 获取过滤后的对象列表。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentActionStatsFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<StudentActionStats> getStudentActionStatsList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentActionStatsFilter") StudentActionStats studentActionStatsFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentActionStatsFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getStudentActionStatsCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentActionStatsFilter") StudentActionStats studentActionStatsFilter);
}
