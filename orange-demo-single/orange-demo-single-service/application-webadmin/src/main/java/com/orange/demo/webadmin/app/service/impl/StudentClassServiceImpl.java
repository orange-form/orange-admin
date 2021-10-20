package com.orange.demo.webadmin.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.orange.demo.webadmin.app.service.*;
import com.orange.demo.webadmin.app.dao.*;
import com.orange.demo.webadmin.app.model.*;
import com.orange.demo.webadmin.upms.service.SysDeptService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 班级数据数据操作服务类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
@Service("studentClassService")
public class StudentClassServiceImpl extends BaseService<StudentClass, Long> implements StudentClassService {

    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private ClassCourseMapper classCourseMapper;
    @Autowired
    private ClassStudentMapper classStudentMapper;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<StudentClass> mapper() {
        return studentClassMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param studentClass 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public StudentClass saveNew(StudentClass studentClass) {
        studentClassMapper.insert(this.buildDefaultValue(studentClass));
        return studentClass;
    }

    /**
     * 利用数据库的insertList语法，批量插入对象列表。
     *
     * @param studentClassList 新增对象列表。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewBatch(List<StudentClass> studentClassList) {
        if (CollUtil.isNotEmpty(studentClassList)) {
            studentClassList.forEach(this::buildDefaultValue);
            studentClassMapper.insertList(studentClassList);
        }
    }

    /**
     * 更新数据对象。
     *
     * @param studentClass         更新的对象。
     * @param originalStudentClass 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(StudentClass studentClass, StudentClass originalStudentClass) {
        studentClass.setCreateUserId(originalStudentClass.getCreateUserId());
        studentClass.setCreateTime(originalStudentClass.getCreateTime());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<StudentClass> uw = this.createUpdateQueryForNullValue(studentClass, studentClass.getClassId());
        return studentClassMapper.update(studentClass, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param classId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long classId) {
        if (studentClassMapper.deleteById(classId) == 0) {
            return false;
        }
        // 开始删除多对多子表的关联
        ClassCourse classCourse = new ClassCourse();
        classCourse.setClassId(classId);
        classCourseMapper.delete(new QueryWrapper<>(classCourse));
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(classId);
        classStudentMapper.delete(new QueryWrapper<>(classStudent));
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentClassListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<StudentClass> getStudentClassList(StudentClass filter, String orderBy) {
        return studentClassMapper.getStudentClassList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getStudentClassList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<StudentClass> getStudentClassListWithRelation(StudentClass filter, String orderBy) {
        List<StudentClass> resultList = studentClassMapper.getStudentClassList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 批量添加多对多关联关系。
     *
     * @param classCourseList 多对多关联表对象集合。
     * @param classId 主表Id。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addClassCourseList(List<ClassCourse> classCourseList, Long classId) {
        for (ClassCourse classCourse : classCourseList) {
            classCourse.setClassId(classId);
            MyModelUtil.setDefaultValue(classCourse, "courseOrder", 0);
            classCourseMapper.insert(classCourse);
        }
    }

    /**
     * 更新中间表数据。
     *
     * @param classCourse 中间表对象。
     * @return 更新成功与否。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateClassCourse(ClassCourse classCourse) {
        ClassCourse filter = new ClassCourse();
        filter.setClassId(classCourse.getClassId());
        filter.setCourseId(classCourse.getCourseId());
        UpdateWrapper<ClassCourse> uw =
                BaseService.createUpdateQueryForNullValue(classCourse, ClassCourse.class);
        uw.setEntity(filter);
        return classCourseMapper.update(classCourse, uw) > 0;
    }

    /**
     * 获取中间表数据。
     *
     * @param classId 主表Id。
     * @param courseId 从表Id。
     * @return 中间表对象。
     */
    @Override
    public ClassCourse getClassCourse(Long classId, Long courseId) {
        ClassCourse filter = new ClassCourse();
        filter.setClassId(classId);
        filter.setCourseId(courseId);
        return classCourseMapper.selectOne(new QueryWrapper<>(filter));
    }

    /**
     * 移除单条多对多关系。
     *
     * @param classId 主表Id。
     * @param courseId 从表Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeClassCourse(Long classId, Long courseId) {
        ClassCourse filter = new ClassCourse();
        filter.setClassId(classId);
        filter.setCourseId(courseId);
        return classCourseMapper.delete(new QueryWrapper<>(filter)) > 0;
    }

    /**
     * 批量添加多对多关联关系。
     *
     * @param classStudentList 多对多关联表对象集合。
     * @param classId 主表Id。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addClassStudentList(List<ClassStudent> classStudentList, Long classId) {
        for (ClassStudent classStudent : classStudentList) {
            classStudent.setClassId(classId);
            classStudentMapper.insert(classStudent);
        }
    }

    /**
     * 移除单条多对多关系。
     *
     * @param classId 主表Id。
     * @param studentId 从表Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeClassStudent(Long classId, Long studentId) {
        ClassStudent filter = new ClassStudent();
        filter.setClassId(classId);
        filter.setStudentId(studentId);
        return classStudentMapper.delete(new QueryWrapper<>(filter)) > 0;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param studentClass 最新数据对象。
     * @param originalStudentClass 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    @Override
    public CallResult verifyRelatedData(StudentClass studentClass, StudentClass originalStudentClass) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(studentClass, originalStudentClass, StudentClass::getSchoolId)
                && !sysDeptService.existId(studentClass.getSchoolId())) {
            return CallResult.error(String.format(errorMessageFormat, "所属校区"));
        }
        //这里是基于字典的验证。
        if (this.needToVerify(studentClass, originalStudentClass, StudentClass::getLeaderId)
                && !studentService.existId(studentClass.getLeaderId())) {
            return CallResult.error(String.format(errorMessageFormat, "学生班长"));
        }
        return CallResult.ok();
    }

    private StudentClass buildDefaultValue(StudentClass studentClass) {
        studentClass.setClassId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        studentClass.setCreateUserId(tokenData.getUserId());
        studentClass.setCreateTime(new Date());
        studentClass.setStatus(GlobalDeletedFlag.NORMAL);
        MyModelUtil.setDefaultValue(studentClass, "finishClassHour", 0);
        return studentClass;
    }
}
