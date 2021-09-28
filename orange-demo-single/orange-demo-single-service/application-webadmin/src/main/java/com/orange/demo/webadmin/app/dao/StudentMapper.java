package com.orange.demo.webadmin.app.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.webadmin.app.model.Student;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 学生数据数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface StudentMapper extends BaseDaoMapper<Student> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param studentFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<Student> getStudentList(
            @Param("studentFilter") Student studentFilter, @Param("orderBy") String orderBy);

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
