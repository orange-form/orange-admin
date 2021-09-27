package com.orange.demo.courseclassservice.service.impl;

import com.orange.demo.courseclassservice.service.*;
import com.orange.demo.courseclassservice.dao.*;
import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 班级数据数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
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
    @Override
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
    @Override
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
    @Override
    public boolean remove(Long classId) {
        // 这里先删除主数据
        if (!this.removeById(classId)) {
            return false;
        }
        // 开始删除多对多中间表的关联
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
    @Override
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
    @Override
    public <M> List<StudentClass> getStudentClassList(
            String inFilterField, Set<M> inFilterValues, StudentClass filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, StudentClass.class);
        return studentClassMapper.getStudentClassList(inFilterColumn, inFilterValues, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getStudentClassList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    @Override
    public List<StudentClass> getStudentClassListWithRelation(StudentClass filter, String orderBy) {
        List<StudentClass> resultList = studentClassMapper.getStudentClassList(null, null, filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
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
    @Override
    public <M> List<StudentClass> getStudentClassListWithRelation(
            String inFilterField, Set<M> inFilterValues, StudentClass filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, StudentClass.class);
        List<StudentClass> resultList =
                studentClassMapper.getStudentClassList(inFilterColumn, inFilterValues, filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly(), batchSize);
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
    @Override
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
    @Override
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
    @Override
    public boolean removeClassCourse(Long classId, Long courseId) {
        ClassCourse filter = new ClassCourse();
        filter.setClassId(classId);
        filter.setCourseId(courseId);
        return classCourseMapper.delete(filter) > 0;
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
    @Override
    public boolean removeClassStudent(Long classId, Long studentId) {
        ClassStudent filter = new ClassStudent();
        filter.setClassId(classId);
        filter.setStudentId(studentId);
        return classStudentMapper.delete(filter) > 0;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param studentClass         最新数据对象。
     * @param originalStudentClass 原有数据对象。
     * @return 数据全部正确返回true，否则false，同时返回具体的错误信息。
     */
    @Override
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
