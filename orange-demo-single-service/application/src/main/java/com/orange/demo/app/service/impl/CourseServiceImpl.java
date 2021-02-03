package com.orange.demo.app.service.impl;

import com.orange.demo.app.service.*;
import com.orange.demo.app.dao.*;
import com.orange.demo.app.model.*;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 课程数据数据操作服务类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Service("courseService")
public class CourseServiceImpl extends BaseService<Course, Long> implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ClassCourseMapper classCourseMapper;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<Course> mapper() {
        return courseMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param course 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Course saveNew(Course course) {
        course.setCourseId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        course.setCreateUserId(tokenData.getUserId());
        Date now = new Date();
        course.setCreateTime(now);
        course.setUpdateTime(now);
        courseMapper.insert(course);
        return course;
    }

    /**
     * 更新数据对象。
     *
     * @param course         更新的对象。
     * @param originalCourse 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Course course, Course originalCourse) {
        course.setCreateUserId(originalCourse.getCreateUserId());
        course.setCreateTime(originalCourse.getCreateTime());
        course.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        return courseMapper.updateByPrimaryKey(course) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param courseId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long courseId) {
        // 这里先删除主数据
        if (!this.removeById(courseId)) {
            return false;
        }
        // 开始删除多对多父表的关联
        ClassCourse classCourse = new ClassCourse();
        classCourse.setCourseId(courseId);
        classCourseMapper.delete(classCourse);
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getCourseListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<Course> getCourseList(Course filter, String orderBy) {
        return courseMapper.getCourseList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getCourseList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<Course> getCourseListWithRelation(Course filter, String orderBy) {
        List<Course> resultList = courseMapper.getCourseList(filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.normal());
        return resultList;
    }

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回不与指定主表主键Id存在对多对关系的列表。
     *
     * @param classId 主表主键Id。
     * @param filter 从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<Course> getNotInCourseListByClassId(
            Long classId, Course filter, String orderBy) {
        List<Course> resultList =
                courseMapper.getNotInCourseListByClassId(classId, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回与指定主表主键Id存在对多对关系的列表。
     *
     * @param classId 主表主键Id。
     * @param filter 从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<Course> getCourseListByClassId(
            Long classId, Course filter, String orderBy) {
        List<Course> resultList =
                courseMapper.getCourseListByClassId(classId, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param course 最新数据对象。
     * @param originalCourse 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    @Override
    public CallResult verifyRelatedData(Course course, Course originalCourse) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(course, originalCourse, Course::getGradeId)
                && !gradeService.existId(course.getGradeId())) {
            return CallResult.error(String.format(errorMessageFormat, "所属年级"));
        }
        return CallResult.ok();
    }
}
