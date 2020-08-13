package com.orange.demo.courseclassservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.courseclassservice.model.Student;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 学生数据数据操作访问接口。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
public interface StudentMapper extends BaseDaoMapper<Student> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<Student> getStudentList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentFilter") Student studentFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getStudentCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentFilter") Student studentFilter);

    /**
     * 根据关联主表Id，获取关联从表数据列表。
     *
     * @param classId 关联主表Id。
     * @param studentFilter 从表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 从表数据列表。
     */
    List<Student> getStudentListByClassId(
            @Param("classId") Long classId,
            @Param("studentFilter") Student studentFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表中没有和主表建立关联关系的数据列表。
     *
     * @param classId 关联主表Id。
     * @param studentFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 与主表没有建立关联的从表数据列表。
     */
    List<Student> getNotInStudentListByClassId(
            @Param("classId") Long classId,
            @Param("studentFilter") Student studentFilter,
            @Param("orderBy") String orderBy);
}
