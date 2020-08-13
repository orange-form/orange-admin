package com.orange.demo.statsservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.statsservice.model.CourseTransStats;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 课程统计数据操作访问接口。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
public interface CourseTransStatsMapper extends BaseDaoMapper<CourseTransStats> {

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
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param courseTransStatsFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<CourseTransStats> getCourseTransStatsList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("courseTransStatsFilter") CourseTransStats courseTransStatsFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param courseTransStatsFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getCourseTransStatsCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("courseTransStatsFilter") CourseTransStats courseTransStatsFilter);
}
