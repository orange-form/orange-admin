package com.orangeforms.webadmin.app.service;

import com.orangeforms.webadmin.app.model.*;
import com.orangeforms.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 班级数据数据操作服务接口。
 *
 * @author Jerry
 * @date 2020-09-24
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
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getStudentClassList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<StudentClass> getStudentClassListWithRelation(StudentClass filter, String orderBy);

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
}
