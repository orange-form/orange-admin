package com.orangeforms.webadmin.app.service;

import com.orangeforms.webadmin.app.model.*;
import com.orangeforms.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 课程统计数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface CourseTransStatsService extends IBaseService<CourseTransStats, Long> {

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getCourseTransStatsListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<CourseTransStats> getCourseTransStatsList(CourseTransStats filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getCourseTransStatsList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<CourseTransStats> getCourseTransStatsListWithRelation(CourseTransStats filter, String orderBy);

    /**
     * 获取分组过滤后的数据查询结果，以及关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     *
     * @param filter      过滤对象。
     * @param groupSelect 分组显示列表参数。位于SQL语句SELECT的后面。
     * @param groupBy     分组参数。位于SQL语句的GROUP BY后面。
     * @param orderBy     排序字符串，ORDER BY从句的参数。
     * @return 分组过滤结果集。
     */
    List<CourseTransStats> getGroupedCourseTransStatsListWithRelation(
            CourseTransStats filter, String groupSelect, String groupBy, String orderBy);
}
