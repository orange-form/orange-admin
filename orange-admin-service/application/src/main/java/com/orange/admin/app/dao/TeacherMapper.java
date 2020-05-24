package com.orange.admin.app.dao;

import com.orange.admin.common.core.annotation.EnableDataPerm;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.app.model.Teacher;
import com.orange.admin.upms.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 老师数据源数据操作访问接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@EnableDataPerm
public interface TeacherMapper extends BaseDaoMapper<Teacher> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param teacherFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<Teacher> getTeacherList(
            @Param("teacherFilter") Teacher teacherFilter, @Param("orderBy") String orderBy);

    /**
     * 获取过滤后的对象列表。同时支持基于一对一从表字段的过滤条件。
     *
     * @param teacherFilter 主表过滤对象。
     * @param sysDeptFilter 一对一从表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<Teacher> getTeacherListEx(
            @Param("teacherFilter") Teacher teacherFilter,
            @Param("sysDeptFilter") SysDept sysDeptFilter,
            @Param("orderBy") String orderBy);
}
