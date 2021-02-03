package com.orange.demo.app.service.impl;

import com.orange.demo.app.service.*;
import com.orange.demo.app.dao.*;
import com.orange.demo.app.model.*;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 学生行为流水数据操作服务类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Service("studentActionTransService")
public class StudentActionTransServiceImpl extends BaseService<StudentActionTrans, Long> implements StudentActionTransService {

    @Autowired
    private StudentActionTransMapper studentActionTransMapper;
    @Autowired
    private SchoolInfoService schoolInfoService;
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
    protected BaseDaoMapper<StudentActionTrans> mapper() {
        return studentActionTransMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param studentActionTrans 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public StudentActionTrans saveNew(StudentActionTrans studentActionTrans) {
        studentActionTrans.setTransId(idGenerator.nextLongId());
        studentActionTransMapper.insert(studentActionTrans);
        return studentActionTrans;
    }

    /**
     * 更新数据对象。
     *
     * @param studentActionTrans         更新的对象。
     * @param originalStudentActionTrans 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(StudentActionTrans studentActionTrans, StudentActionTrans originalStudentActionTrans) {
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        return studentActionTransMapper.updateByPrimaryKey(studentActionTrans) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param transId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long transId) {
        // 这里先删除主数据
        return this.removeById(transId);
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getStudentActionTransListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<StudentActionTrans> getStudentActionTransList(StudentActionTrans filter, String orderBy) {
        return studentActionTransMapper.getStudentActionTransList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getStudentActionTransList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<StudentActionTrans> getStudentActionTransListWithRelation(StudentActionTrans filter, String orderBy) {
        List<StudentActionTrans> resultList = studentActionTransMapper.getStudentActionTransList(filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.normal());
        return resultList;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param studentActionTrans 最新数据对象。
     * @param originalStudentActionTrans 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    @Override
    public CallResult verifyRelatedData(StudentActionTrans studentActionTrans, StudentActionTrans originalStudentActionTrans) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(studentActionTrans, originalStudentActionTrans, StudentActionTrans::getSchoolId)
                && !schoolInfoService.existId(studentActionTrans.getSchoolId())) {
            return CallResult.error(String.format(errorMessageFormat, "学生校区"));
        }
        //这里是基于字典的验证。
        if (this.needToVerify(studentActionTrans, originalStudentActionTrans, StudentActionTrans::getGradeId)
                && !gradeService.existId(studentActionTrans.getGradeId())) {
            return CallResult.error(String.format(errorMessageFormat, "学生年级"));
        }
        return CallResult.ok();
    }
}
