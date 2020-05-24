package com.orange.admin.app.dao;

import com.orange.admin.common.core.annotation.EnableDataPerm;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.app.model.TeacherTransStats;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 老师流水统计数据操作访问接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@EnableDataPerm
public interface TeacherTransStatsMapper extends BaseDaoMapper<TeacherTransStats> {

    /**
     * 获取分组计算后的数据对象列表。
     *
     * @param teacherTransStatsFilter 主表过滤对象。
     * @param groupSelect 分组显示字段列表字符串，SELECT从句的参数。
     * @param groupBy 分组字段列表字符串，GROUP BY从句的参数。
     * @param orderBy 排序字符串，ORDER BY从句的参数。
     * @return 对象列表。
     */
    List<TeacherTransStats> getGroupedTeacherTransStatsList(
            @Param("teacherTransStatsFilter") TeacherTransStats teacherTransStatsFilter,
            @Param("groupSelect") String groupSelect,
            @Param("groupBy") String groupBy,
            @Param("orderBy") String orderBy);

    /**
     * 获取过滤后的对象列表。
     *
     * @param teacherTransStatsFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<TeacherTransStats> getTeacherTransStatsList(
            @Param("teacherTransStatsFilter") TeacherTransStats teacherTransStatsFilter, @Param("orderBy") String orderBy);
}
