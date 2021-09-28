package com.orange.demo.courseclassservice.service;

import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 课程数据数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface CourseService extends IBaseService<Course, Long> {

    /**
     * 保存新增对象。
     *
     * @param course 新增对象。
     * @return 返回新增对象。
     */
    Course saveNew(Course course);

    /**
     * 更新数据对象。
     *
     * @param course         更新的对象。
     * @param originalCourse 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(Course course, Course originalCourse);

    /**
     * 删除指定数据。
     *
     * @param courseId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long courseId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getCourseListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<Course> getCourseList(Course filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getCourseListWithRelation)方法。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    <M> List<Course> getCourseList(String inFilterField, Set<M> inFilterValues, Course filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getCourseList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    List<Course> getCourseListWithRelation(Course filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 同时还包含主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getCourseList)，以便获取更好的查询性能。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    <M> List<Course> getCourseListWithRelation(
            String inFilterField, Set<M> inFilterValues, Course filter, String orderBy);

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回不与指定主表主键Id存在对多对关系的列表。
     *
     * @param classId 主表的关联键Id。
     * @param filter         从表的过滤对象。
     * @param orderBy        排序参数。
     * @return 查询结果集。
     */
    List<Course> getNotInCourseListByClassId(Long classId, Course filter, String orderBy);

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回与指定主表主键Id存在对多对关系的列表。
     *
     * @param classId 主表的关联键Id。
     * @param filter 从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<Course> getCourseListByClassId(Long classId, Course filter, String orderBy);

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param course         最新数据对象。
     * @param originalCourse 原有数据对象。
     * @return 数据全部正确返回true，否则false，同时返回具体的错误信息。
     */
    CallResult verifyRelatedData(Course course, Course originalCourse);
}
