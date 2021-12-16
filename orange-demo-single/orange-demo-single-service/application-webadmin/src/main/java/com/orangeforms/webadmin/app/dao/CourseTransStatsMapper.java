package com.orangeforms.webadmin.app.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.webadmin.app.model.CourseTransStats;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 课程统计数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface CourseTransStatsMapper extends BaseDaoMapper<CourseTransStats> {

    /**
     * 批量插入对象列表。
     *
     * @param courseTransStatsList 新增对象列表。
     */
    void insertList(List<CourseTransStats> courseTransStatsList);

    /**
     * 获取分组计算后的数据对象列表。
     *
     * @param courseTransStatsFilter 主表过滤对象。
     * @param groupSelect 分组显示字段列表字符串，SELECT从句的参数。
     * @param groupBy 分组字段列表字符串，GROUP BY从句的参数。
     * @param orderBy 排序字符串，ORDER BY从句的参数。
     * @return 对象列表。
     */
    List<CourseTransStats> getGroupedCourseTransStatsList(
            @Param("courseTransStatsFilter") CourseTransStats courseTransStatsFilter,
            @Param("groupSelect") String groupSelect,
            @Param("groupBy") String groupBy,
            @Param("orderBy") String orderBy);

    /**
     * 获取过滤后的对象列表。
     *
     * @param courseTransStatsFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<CourseTransStats> getCourseTransStatsList(
            @Param("courseTransStatsFilter") CourseTransStats courseTransStatsFilter, @Param("orderBy") String orderBy);
}
