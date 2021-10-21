package com.orange.demo.courseclassservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.courseclassservice.model.Course;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 课程数据数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface CourseMapper extends BaseDaoMapper<Course> {

    /**
     * 批量插入对象列表。
     *
     * @param courseList 新增对象列表。
     */
    void insertList(List<Course> courseList);

    /**
     * 获取过滤后的对象列表。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param courseFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<Course> getCourseList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("courseFilter") Course courseFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param courseFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getCourseCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("courseFilter") Course courseFilter);

    /**
     * 根据关联主表Id，获取关联从表数据列表。
     *
     * @param classId 关联主表Id。
     * @param courseFilter 从表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 从表数据列表。
     */
    List<Course> getCourseListByClassId(
            @Param("classId") Long classId,
            @Param("courseFilter") Course courseFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表中没有和主表建立关联关系的数据列表。
     *
     * @param classId 关联主表Id。
     * @param courseFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 与主表没有建立关联的从表数据列表。
     */
    List<Course> getNotInCourseListByClassId(
            @Param("classId") Long classId,
            @Param("courseFilter") Course courseFilter,
            @Param("orderBy") String orderBy);
}
