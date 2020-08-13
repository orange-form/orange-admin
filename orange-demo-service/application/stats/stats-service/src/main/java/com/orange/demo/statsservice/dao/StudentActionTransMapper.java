package com.orange.demo.statsservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.statsservice.model.StudentActionTrans;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 学生行为流水数据操作访问接口。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
public interface StudentActionTransMapper extends BaseDaoMapper<StudentActionTrans> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentActionTransFilter 过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    <M> List<StudentActionTrans> getStudentActionTransList(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentActionTransFilter") StudentActionTrans studentActionTransFilter,
            @Param("orderBy") String orderBy);

    /**
     * 获取对象列表，过滤条件中包含like和between条件，以及指定属性的(in list)过滤条件。
     *
     * @param inFilterColumn 参与(In-list)过滤的数据表列。
     * @param inFilterValues 参与(In-list)过滤的数据表列值集合。
     * @param studentActionTransFilter 过滤对象。
     * @return 对象列表。
     */
    <M> Integer getStudentActionTransCount(
            @Param("inFilterColumn") String inFilterColumn,
            @Param("inFilterValues") Set<M> inFilterValues,
            @Param("studentActionTransFilter") StudentActionTrans studentActionTransFilter);
}
