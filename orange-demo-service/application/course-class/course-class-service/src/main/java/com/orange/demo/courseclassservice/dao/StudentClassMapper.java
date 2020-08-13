package com.orange.demo.courseclassservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.courseclassservice.model.StudentClass;
import com.orange.demo.courseclassservice.model.ClassCourse;
import com.orange.demo.courseclassservice.model.ClassStudent;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 班级数据数据操作访问接口。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
public interface StudentClassMapper extends BaseDaoMapper<StudentClass> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentClassFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<StudentClass> getStudentClassList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentClassFilter") StudentClass studentClassFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentClassFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getStudentClassCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentClassFilter") StudentClass studentClassFilter);

    /**
     * 插入一组关联关系数据。
     *
     * @param classCourseList 关联关系数据项列表。
     */
    void addClassCourseList(List<ClassCourse> classCourseList);

    /**
     * 插入一组关联关系数据。
     *
     * @param classStudentList 关联关系数据项列表。
     */
    void addClassStudentList(List<ClassStudent> classStudentList);
}
