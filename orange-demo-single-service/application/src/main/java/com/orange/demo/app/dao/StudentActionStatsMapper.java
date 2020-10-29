package com.orange.demo.app.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.app.model.StudentActionStats;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 学生行为统计数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface StudentActionStatsMapper extends BaseDaoMapper<StudentActionStats> {

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
     * @param studentActionStatsFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<StudentActionStats> getStudentActionStatsList(
            @Param("studentActionStatsFilter") StudentActionStats studentActionStatsFilter, @Param("orderBy") String orderBy);
}
