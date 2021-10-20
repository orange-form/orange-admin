package com.orange.demo.webadmin.app.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.webadmin.app.model.StudentClass;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 班级数据数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
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
     * @param studentClassFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<StudentClass> getStudentClassList(
            @Param("studentClassFilter") StudentClass studentClassFilter, @Param("orderBy") String orderBy);
}
