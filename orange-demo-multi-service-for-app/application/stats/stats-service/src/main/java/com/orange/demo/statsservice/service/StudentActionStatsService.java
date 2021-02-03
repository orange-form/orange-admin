package com.orange.demo.statsservice.service;

import com.orange.demo.statsservice.model.*;
import com.orange.demo.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 学生行为统计数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface StudentActionStatsService extends IBaseService<StudentActionStats, Long> {

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentActionStatsListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<StudentActionStats> getStudentActionStatsList(StudentActionStats filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentActionStatsListWithRelation)方法。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    <M> List<StudentActionStats> getStudentActionStatsList(String inFilterField, Set<M> inFilterValues, StudentActionStats filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getStudentActionStatsList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    List<StudentActionStats> getStudentActionStatsListWithRelation(StudentActionStats filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 同时还包含主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getStudentActionStatsList)，以便获取更好的查询性能。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    <M> List<StudentActionStats> getStudentActionStatsListWithRelation(
            String inFilterField, Set<M> inFilterValues, StudentActionStats filter, String orderBy);

    /**
     * 获取分组过滤后的数据查询结果，以及关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     *
     * @param filter         过滤对象。
     * @param groupSelect    分组显示列表参数。位于SQL语句SELECT的后面。
     * @param groupBy        分组参数。位于SQL语句的GROUP BY后面。
     * @param orderBy        排序字符串，ORDER BY从句的参数。
     * @return 分组过滤结果集。
     */
    List<StudentActionStats> getGroupedStudentActionStatsListWithRelation(
            StudentActionStats filter, String groupSelect, String groupBy, String orderBy);
}
