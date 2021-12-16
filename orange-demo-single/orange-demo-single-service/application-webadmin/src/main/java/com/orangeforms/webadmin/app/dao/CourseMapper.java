package com.orangeforms.webadmin.app.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.webadmin.app.model.Course;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 课程数据数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
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
     * @param courseFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<Course> getCourseList(
            @Param("courseFilter") Course courseFilter, @Param("orderBy") String orderBy);

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
