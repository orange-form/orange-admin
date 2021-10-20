package com.orange.demo.webadmin.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.orange.demo.webadmin.app.service.*;
import com.orange.demo.webadmin.app.dao.*;
import com.orange.demo.webadmin.app.model.*;
import com.orange.demo.webadmin.upms.service.SysDeptService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service("studentActionTransService")
public class StudentActionTransServiceImpl extends BaseService<StudentActionTrans, Long> implements StudentActionTransService {

    @Autowired
    private StudentActionTransMapper studentActionTransMapper;
    @Autowired
    private SysDeptService sysDeptService;
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
        studentActionTransMapper.insert(this.buildDefaultValue(studentActionTrans));
        return studentActionTrans;
    }

    /**
     * 利用数据库的insertList语法，批量插入对象列表。
     *
     * @param studentActionTransList 新增对象列表。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewBatch(List<StudentActionTrans> studentActionTransList) {
        if (CollUtil.isNotEmpty(studentActionTransList)) {
            studentActionTransList.forEach(this::buildDefaultValue);
            studentActionTransMapper.insertList(studentActionTransList);
        }
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
        UpdateWrapper<StudentActionTrans> uw = this.createUpdateQueryForNullValue(studentActionTrans, studentActionTrans.getTransId());
        return studentActionTransMapper.update(studentActionTrans, uw) == 1;
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
        return studentActionTransMapper.deleteById(transId) == 1;
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
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
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
                && !sysDeptService.existId(studentActionTrans.getSchoolId())) {
            return CallResult.error(String.format(errorMessageFormat, "学生校区"));
        }
        //这里是基于字典的验证。
        if (this.needToVerify(studentActionTrans, originalStudentActionTrans, StudentActionTrans::getGradeId)
                && !gradeService.existId(studentActionTrans.getGradeId())) {
            return CallResult.error(String.format(errorMessageFormat, "学生年级"));
        }
        return CallResult.ok();
    }

    private StudentActionTrans buildDefaultValue(StudentActionTrans studentActionTrans) {
        studentActionTrans.setTransId(idGenerator.nextLongId());
        return studentActionTrans;
    }
}
