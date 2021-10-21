package com.orange.demo.courseclassservice.service;

import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 班级数据数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface StudentClassService extends IBaseService<StudentClass, Long> {

    /**
     * 保存新增对象。
     *
     * @param studentClass 新增对象。
     * @return 返回新增对象。
     */
    StudentClass saveNew(StudentClass studentClass);

    /**
     * 利用数据库的insertList语法，批量插入对象列表。
     *
     * @param studentClassList 新增对象列表。
     */
    void saveNewBatch(List<StudentClass> studentClassList);

    /**
     * 更新数据对象。
     *
     * @param studentClass         更新的对象。
     * @param originalStudentClass 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(StudentClass studentClass, StudentClass originalStudentClass);

    /**
     * 删除指定数据。
     *
     * @param classId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long classId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentClassListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<StudentClass> getStudentClassList(StudentClass filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentClassListWithRelation)方法。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    <M> List<StudentClass> getStudentClassList(String inFilterField, Set<M> inFilterValues, StudentClass filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getStudentClassList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    List<StudentClass> getStudentClassListWithRelation(StudentClass filter, String orderBy);

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 同时还包含主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getStudentClassList)，以便获取更好的查询性能。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    <M> List<StudentClass> getStudentClassListWithRelation(
            String inFilterField, Set<M> inFilterValues, StudentClass filter, String orderBy);

    /**
     * 批量添加多对多关联关系。
     *
     * @param classCourseList 多对多关联表对象集合。
     * @param classId 主表Id。
     */
    void addClassCourseList(List<ClassCourse> classCourseList, Long classId);

    /**
     * 更新中间表数据。
     *
     * @param classCourse 中间表对象。
     * @return 更新成功与否。
     */
    boolean updateClassCourse(ClassCourse classCourse);

    /**
     * 获取中间表数据。
     *
     * @param classId 主表Id。
     * @param courseId 从表Id。
     * @return 中间表对象。
     */
    ClassCourse getClassCourse(Long classId, Long courseId);

    /**
     * 移除单条多对多关系。
     *
     * @param classId 主表Id。
     * @param courseId 从表Id。
     * @return 成功返回true，否则false。
     */
    boolean removeClassCourse(Long classId, Long courseId);

    /**
     * 批量添加多对多关联关系。
     *
     * @param classStudentList 多对多关联表对象集合。
     * @param classId 主表Id。
     */
    void addClassStudentList(List<ClassStudent> classStudentList, Long classId);

    /**
     * 移除单条多对多关系。
     *
     * @param classId 主表Id。
     * @param studentId 从表Id。
     * @return 成功返回true，否则false。
     */
    boolean removeClassStudent(Long classId, Long studentId);

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param studentClass         最新数据对象。
     * @param originalStudentClass 原有数据对象。
     * @return 数据全部正确返回true，否则false，同时返回具体的错误信息。
     */
    CallResult verifyRelatedData(StudentClass studentClass, StudentClass originalStudentClass);

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的远程字典数据和多对一主表数据是否都是合法数据。
     *
     * @param studentClass         最新数据对象。
     * @param originalStudentClass 原有数据对象。
     * @return 数据全部正确返回true，否则false，同时返回具体的错误信息。
     */
    CallResult verifyRemoteRelatedData(StudentClass studentClass, StudentClass originalStudentClass);
}
