package com.orange.admin.app.service;

import com.orange.admin.app.dao.*;
import com.orange.admin.app.model.*;
import com.orange.admin.upms.model.*;
import com.orange.admin.upms.service.SysUserService;
import com.orange.admin.upms.service.SysDeptService;
import com.orange.admin.common.core.base.service.BaseService;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.object.MyWhereCriteria;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.common.biz.util.BasicIdGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 老师数据源数据操作服务类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Service
public class TeacherService extends BaseService<Teacher, Long> {

    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private BasicIdGenerator idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<Teacher> mapper() {
        return teacherMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param teacher 新增对象。
     * @return 返回新增对象。
     */
    @Transactional
    public Teacher saveNew(Teacher teacher) {
        teacher.setTeacherId(idGenerator.nextLongId());
        teacher.setRegisterDate(new Date());
        teacherMapper.insert(teacher);
        return teacher;
    }

    /**
     * 更新数据对象。
     *
     * @param teacher 更新的对象。
     * @param originalTeacher 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional
    public boolean update(Teacher teacher, Teacher originalTeacher) {
        teacher.setRegisterDate(originalTeacher.getRegisterDate());
        return teacherMapper.updateByPrimaryKey(teacher) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param teacherId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional
    public boolean remove(Long teacherId) {
        Teacher teacher = teacherMapper.selectByPrimaryKey(teacherId);
        if (teacher == null) {
            return false;
        }
        // 这里先删除主数据
        if (teacherMapper.deleteByPrimaryKey(teacherId) == 0) {
            return false;
        }
        // 这里可继续删除关联数据。
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getTeacherListWithRelation)方法。
     *
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<Teacher> getTeacherList(Teacher filter, String orderBy) {
        return teacherMapper.getTeacherList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getTeacherList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param sysDeptFilter 一对一从表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<Teacher> getTeacherListWithRelation(Teacher filter, SysDept sysDeptFilter, String orderBy) {
        List<Teacher> resultList =
                teacherMapper.getTeacherListEx(filter, sysDeptFilter, orderBy);
        Map<String, List<MyWhereCriteria>> criteriaMap = buildAggregationAdditionalWhereCriteria();
        this.buildAllRelationForDataList(resultList, false, criteriaMap);
        return resultList;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param teacher 最新数据对象。
     * @param originalTeacher 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    public VerifyResult verifyRelatedData(Teacher teacher, Teacher originalTeacher) {
        String errorMessage = null;
        do {
            //这里是基于字典的验证。
            if (this.needToVerify(teacher, originalTeacher, Teacher::getSchoolId)) {
                if (!sysDeptService.existId(teacher.getSchoolId())) {
                    errorMessage = "数据验证失败，关联的所属校区并不存在，请刷新后重试！";
                    break;
                }
            }
            //这里是基于字典的验证。
            if (this.needToVerify(teacher, originalTeacher, Teacher::getUserId)) {
                if (!sysUserService.existId(teacher.getUserId())) {
                    errorMessage = "数据验证失败，关联的绑定用户并不存在，请刷新后重试！";
                    break;
                }
            }
        } while (false);
        return VerifyResult.create(errorMessage);
    }
}