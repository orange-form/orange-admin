package com.orangeforms.courseclassservice.dao;

import com.orangeforms.common.core.annotation.EnableDataPerm;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.courseclassservice.model.StudentClass;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 班级数据数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@EnableDataPerm
public interface StudentClassMapper extends BaseDaoMapper<StudentClass> {

    /**
     * 批量插入对象列表。
     *
     * @param studentClassList 新增对象列表。
     */
    void insertList(List<StudentClass> studentClassList);

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
}
