package com.orange.demo.app.service;

import com.orange.demo.app.dao.*;
import com.orange.demo.app.model.*;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.object.MyWhereCriteria;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 班级数据数据操作服务类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Service
public class StudentClassService extends BaseService<StudentClass, Long> {

    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private ClassCourseMapper classCourseMapper;
    @Autowired
    private ClassStudentMapper classStudentMapper;
    @Autowired
    private SchoolInfoService schoolInfoService;
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
    public StudentClass saveNew(StudentClass studentClass) {
        studentClass.setClassId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        studentClass.setCreateUserId(tokenData.getUserId());
        studentClass.setCreateTime(new Date());
        studentClass.setStatus(GlobalDeletedFlag.NORMAL);
        if (studentClass.getFinishClassHour() == null) {
            studentClass.setFinishClassHour(0);
        }
        studentClassMapper.insert(studentClass);
        return studentClass;
    }

    /**
     * 更新数据对象。
     *
     * @param studentClass         更新的对象。
     * @param originalStudentClass 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(StudentClass studentClass, StudentClass originalStudentClass) {
        studentClass.setCreateUserId(originalStudentClass.getCreateUserId());
        studentClass.setCreateTime(originalStudentClass.getCreateTime());
        studentClass.setStatus(GlobalDeletedFlag.NORMAL);
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        return studentClassMapper.updateByPrimaryKey(studentClass) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param classId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long classId) {
        Example studentClassExample = new Example(StudentClass.class);
        Example.Criteria c = studentClassExample.createCriteria();
        c.andEqualTo(super.idFieldName, classId);
        c.andEqualTo(super.deletedFlagFieldName, GlobalDeletedFlag.NORMAL);
        // 这里先删除主数据
        StudentClass deletedObject = new StudentClass();
        deletedObject.setStatus(GlobalDeletedFlag.DELETED);
        if (studentClassMapper.updateByExampleSelective(deletedObject, studentClassExample) == 0) {
            return false;
        }
        // 开始删除多对多子表的关联
        ClassCourse classCourse = new ClassCourse();
        classCourse.setClassId(classId);
        classCourseMapper.delete(classCourse);
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(classId);
        classStudentMapper.delete(classStudent);
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
    public List<StudentClass> getStudentClassList(StudentClass filter, String orderBy) {
        return studentClassMapper.getStudentClassList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getStudentClassList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<StudentClass> getStudentClassListWithRelation(StudentClass filter, String orderBy) {
        List<StudentClass> resultList = studentClassMapper.getStudentClassList(filter, orderBy);
        Map<String, List<MyWhereCriteria>> criteriaMap = buildAggregationAdditionalWhereCriteria();
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), criteriaMap);
        return resultList;
    }

    /**
     * 批量添加多对多关联关系。
     *
     * @param classCourseList 多对多关联表对象集合。
     * @param classId 主表Id。
     */
    @Transactional(rollbackFor = Exception.class)
    public void addClassCourseList(List<ClassCourse> classCourseList, Long classId) {
        for (ClassCourse classCourse : classCourseList) {
            classCourse.setClassId(classId);
            if (classCourse.getCourseOrder() == null) {
                classCourse.setCourseOrder(0);
            }
        }
        classCourseMapper.insertList(classCourseList);
    }

    /**
     * 更新中间表数据。
     *
     * @param classCourse 中间表对象。
     * @return 更新成功与否。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateClassCourse(ClassCourse classCourse) {
        Example e = new Example(ClassCourse.class);
        e.createCriteria()
                .andEqualTo("classId", classCourse.getClassId())
                .andEqualTo("courseId", classCourse.getCourseId());
        return classCourseMapper.updateByExample(classCourse, e) > 0;
    }

    /**
     * 获取中间表数据。
     *
     * @param classId 主表Id。
     * @param courseId 从表Id。
     * @return 中间表对象。
     */
    public ClassCourse getClassCourse(Long classId, Long courseId) {
        Example e = new Example(ClassCourse.class);
        e.createCriteria()
                .andEqualTo("classId", classId)
                .andEqualTo("courseId", courseId);
        return classCourseMapper.selectOneByExample(e);
    }

    /**
     * 移除单条多对多关系。
     *
     * @param classId 主表Id。
     * @param courseId 从表Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeClassCourse(Long classId, Long courseId) {
        ClassCourse classCourse = new ClassCourse();
        classCourse.setClassId(classId);
        classCourse.setCourseId(courseId);
        return classCourseMapper.delete(classCourse) > 0;
    }

    /**
     * 批量添加多对多关联关系。
     *
     * @param classStudentList 多对多关联表对象集合。
     * @param classId 主表Id。
     */
    @Transactional(rollbackFor = Exception.class)
    public void addClassStudentList(List<ClassStudent> classStudentList, Long classId) {
        for (ClassStudent classStudent : classStudentList) {
            classStudent.setClassId(classId);
        }
        classStudentMapper.insertList(classStudentList);
    }

    /**
     * 移除单条多对多关系。
     *
     * @param classId 主表Id。
     * @param studentId 从表Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeClassStudent(Long classId, Long studentId) {
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(classId);
        classStudent.setStudentId(studentId);
        return classStudentMapper.delete(classStudent) > 0;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param studentClass 最新数据对象。
     * @param originalStudentClass 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    public CallResult verifyRelatedData(StudentClass studentClass, StudentClass originalStudentClass) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(studentClass, originalStudentClass, StudentClass::getSchoolId)
                && !schoolInfoService.existId(studentClass.getSchoolId())) {
            return CallResult.error(String.format(errorMessageFormat, "所属校区"));
        }
        //这里是基于字典的验证。
        if (this.needToVerify(studentClass, originalStudentClass, StudentClass::getLeaderId)
                && !studentService.existId(studentClass.getLeaderId())) {
            return CallResult.error(String.format(errorMessageFormat, "学生班长"));
        }
        return CallResult.ok();
    }
}
