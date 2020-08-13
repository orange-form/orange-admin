package com.orange.demo.courseclassservice.service;

import com.orange.demo.courseclassservice.dao.*;
import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.courseclassinterface.dto.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.object.MyWhereCriteria;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 班级数据数据操作服务类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Service
public class StudentClassService extends BaseService<StudentClass, StudentClassDto, Long> {

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
        // 这里可继续删除关联数据。
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
        return studentClassMapper.getStudentClassList(null, null, filter, orderBy);
    }

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
    public <M> List<StudentClass> getStudentClassList(
            String inFilterField, Set<M> inFilterValues, StudentClass filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, StudentClass.class);
        return studentClassMapper.getStudentClassList(inFilterColumn, inFilterValues, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getStudentClassList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    public List<StudentClass> getStudentClassListWithRelation(StudentClass filter, String orderBy) {
        List<StudentClass> resultList = studentClassMapper.getStudentClassList(null, null, filter, orderBy);
        Map<String, List<MyWhereCriteria>> criteriaMap = buildAggregationAdditionalWhereCriteria();
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), criteriaMap);
        return resultList;
    }

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
    public <M> List<StudentClass> getStudentClassListWithRelation(
            String inFilterField, Set<M> inFilterValues, StudentClass filter, String orderBy) {
        List<StudentClass> resultList =
                studentClassMapper.getStudentClassList(inFilterField, inFilterValues, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly(), null);
        return resultList;
    }

    /**
     * 批量添加多对多关联关系。
     *
     * @param classCourseList 多对多关联表对象集合。
     */
    @Transactional(rollbackFor = Exception.class)
    public void addClassCourseList(List<ClassCourse> classCourseList) {
        studentClassMapper.addClassCourseList(classCourseList);
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
     */
    @Transactional(rollbackFor = Exception.class)
    public void addClassStudentList(List<ClassStudent> classStudentList) {
        studentClassMapper.addClassStudentList(classStudentList);
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
     * @param studentClass         最新数据对象。
     * @param originalStudentClass 原有数据对象。
     * @return 数据全部正确返回true，否则false，同时返回具体的错误信息。
     */
    public CallResult verifyRelatedData(StudentClass studentClass, StudentClass originalStudentClass) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        if (this.needToVerify(studentClass, originalStudentClass, StudentClass::getSchoolId)
                && !schoolInfoService.existId(studentClass.getSchoolId())) {
            return CallResult.error(String.format(errorMessageFormat, "所属校区"));
        }
        if (this.needToVerify(studentClass, originalStudentClass, StudentClass::getLeaderId)
                && !studentService.existId(studentClass.getLeaderId())) {
            return CallResult.error(String.format(errorMessageFormat, "班长"));
        }
        return CallResult.ok();
    }
}
