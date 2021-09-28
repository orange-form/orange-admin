package com.orange.demo.courseclassservice.service.impl;

import com.orange.demo.application.common.constant.StudentStatus;
import com.orange.demo.courseclassservice.service.*;
import com.orange.demo.courseclassservice.dao.*;
import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 学生数据数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@Service("studentService")
public class StudentServiceImpl extends BaseService<Student, Long> implements StudentService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private ClassStudentMapper classStudentMapper;
    @Autowired
    private SchoolInfoService schoolInfoService;
    @Autowired
    private AreaCodeService areaCodeService;
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
    protected BaseDaoMapper<Student> mapper() {
        return studentMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param student 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Student saveNew(Student student) {
        student.setStudentId(idGenerator.nextLongId());
        MyModelUtil.setDefaultValue(student, "totalCoin", 0);
        MyModelUtil.setDefaultValue(student, "leftCoin", 0);
        MyModelUtil.setDefaultValue(student, "status", StudentStatus.NORMAL);
        studentMapper.insert(student);
        return student;
    }

    /**
     * 更新数据对象。
     *
     * @param student         更新的对象。
     * @param originalStudent 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Student student, Student originalStudent) {
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        return studentMapper.updateByPrimaryKey(student) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param studentId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long studentId) {
        // 这里先删除主数据
        if (!this.removeById(studentId)) {
            return false;
        }
        // 开始删除与本地多对多父表的关联
        ClassStudent classStudent = new ClassStudent();
        classStudent.setStudentId(studentId);
        classStudentMapper.delete(classStudent);
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<Student> getStudentList(Student filter, String orderBy) {
        return studentMapper.getStudentList(null, null, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentListWithRelation)方法。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public <M> List<Student> getStudentList(
            String inFilterField, Set<M> inFilterValues, Student filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, Student.class);
        return studentMapper.getStudentList(inFilterColumn, inFilterValues, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getStudentList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    @Override
    public List<Student> getStudentListWithRelation(Student filter, String orderBy) {
        List<Student> resultList = studentMapper.getStudentList(null, null, filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 同时还包含主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getStudentList)，以便获取更好的查询性能。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    @Override
    public <M> List<Student> getStudentListWithRelation(
            String inFilterField, Set<M> inFilterValues, Student filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, Student.class);
        List<Student> resultList =
                studentMapper.getStudentList(inFilterColumn, inFilterValues, filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly(), batchSize);
        return resultList;
    }

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回不与指定主表主键Id存在对多对关系的列表。
     *
     * @param classId 主表的关联键Id。
     * @param filter         从表的过滤对象。
     * @param orderBy        排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<Student> getNotInStudentListByClassId(Long classId, Student filter, String orderBy) {
        List<Student> resultList =
                studentMapper.getNotInStudentListByClassId(classId, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    /**
     * 在多对多关系中，当前Service的数据表为从表，返回与指定主表主键Id存在对多对关系的列表。
     *
     * @param classId 主表的关联键Id。
     * @param filter 从表的过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<Student> getStudentListByClassId(Long classId, Student filter, String orderBy) {
        List<Student> resultList =
                studentMapper.getStudentListByClassId(classId, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param student         最新数据对象。
     * @param originalStudent 原有数据对象。
     * @return 数据全部正确返回true，否则false，同时返回具体的错误信息。
     */
    @Override
    public CallResult verifyRelatedData(Student student, Student originalStudent) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        if (this.needToVerify(student, originalStudent, Student::getProvinceId)
                && !areaCodeService.existId(student.getProvinceId())) {
            return CallResult.error(String.format(errorMessageFormat, "所在省份"));
        }
        if (this.needToVerify(student, originalStudent, Student::getCityId)
                && !areaCodeService.existId(student.getCityId())) {
            return CallResult.error(String.format(errorMessageFormat, "所在城市"));
        }
        if (this.needToVerify(student, originalStudent, Student::getDistrictId)
                && !areaCodeService.existId(student.getDistrictId())) {
            return CallResult.error(String.format(errorMessageFormat, "所在区县"));
        }
        if (this.needToVerify(student, originalStudent, Student::getGradeId)
                && !gradeService.existId(student.getGradeId())) {
            return CallResult.error(String.format(errorMessageFormat, "年级"));
        }
        if (this.needToVerify(student, originalStudent, Student::getSchoolId)
                && !schoolInfoService.existId(student.getSchoolId())) {
            return CallResult.error(String.format(errorMessageFormat, "所属校区"));
        }
        return CallResult.ok();
    }
}
