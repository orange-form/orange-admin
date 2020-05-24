package com.orange.admin.app.service;

import com.orange.admin.app.dao.*;
import com.orange.admin.app.model.*;
import com.orange.admin.upms.service.SysDeptService;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.object.MyWhereCriteria;
import com.orange.admin.common.core.object.MyRelationParam;
import com.orange.admin.common.biz.base.service.BaseBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 老师流水统计数据操作服务类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Service
public class TeacherTransStatsService extends BaseBizService<TeacherTransStats, Long> {

    @Autowired
    private TeacherTransStatsMapper teacherTransStatsMapper;
    @Autowired
    private AreaCodeService areaCodeService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private TeacherService teacherService;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<TeacherTransStats> mapper() {
        return teacherTransStatsMapper;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getTeacherTransStatsListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<TeacherTransStats> getTeacherTransStatsList(TeacherTransStats filter, String orderBy) {
        return teacherTransStatsMapper.getTeacherTransStatsList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getTeacherTransStatsList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<TeacherTransStats> getTeacherTransStatsListWithRelation(TeacherTransStats filter, String orderBy) {
        List<TeacherTransStats> resultList = teacherTransStatsMapper.getTeacherTransStatsList(filter, orderBy);
        Map<String, List<MyWhereCriteria>> criteriaMap = buildAggregationAdditionalWhereCriteria();
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), criteriaMap);
        return resultList;
    }

    /**
     * 获取分组过滤后的数据查询结果，以及关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     *
     * @param filter      过滤对象。
     * @param groupSelect 分组显示列表参数。位于SQL语句SELECT的后面。
     * @param groupBy     分组参数。位于SQL语句的GROUP BY后面。
     * @param orderBy     排序字符串，ORDER BY从句的参数。
     * @return 分组过滤结果集。
     */
    public List<TeacherTransStats> getGroupedTeacherTransStatsListWithRelation(
            TeacherTransStats filter, String groupSelect, String groupBy, String orderBy) {
        List<TeacherTransStats> resultList =
                teacherTransStatsMapper.getGroupedTeacherTransStatsList(filter, groupSelect, groupBy, orderBy);
        // NOTE: 这里只是包含了关联数据，聚合计算数据没有包含。
        // 主要原因是，由于聚合字段通常被视为普通字段使用，不会在group by的从句中出现，语义上也不会在此关联。
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), null);
        return resultList;
    }
}
