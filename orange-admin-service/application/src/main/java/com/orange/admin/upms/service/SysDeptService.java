package com.orange.admin.upms.service;

import com.orange.admin.upms.dao.*;
import com.orange.admin.upms.model.*;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.constant.GlobalDeletedFlag;
import com.orange.admin.common.core.object.TokenData;
import com.orange.admin.common.core.object.MyWhereCriteria;
import com.orange.admin.common.core.object.MyRelationParam;
import com.orange.admin.common.biz.base.service.BaseBizService;
import com.orange.admin.common.biz.util.BasicIdGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 部门管理数据操作服务类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Service
public class SysDeptService extends BaseBizService<SysDept, Long> {

    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private SysDataPermDeptMapper sysDataPermDeptMapper;
    @Autowired
    private BasicIdGenerator idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysDept> mapper() {
        return sysDeptMapper;
    }

    /**
     * 保存新增的部门对象。
     *
     * @param sysDept 新增的部门对象。
     * @return 新增后的部门对象。
     */
    @Transactional(rollbackFor = Exception.class)
    public SysDept saveNew(SysDept sysDept) {
        sysDept.setDeptId(idGenerator.nextLongId());
        sysDept.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        TokenData tokenData = TokenData.takeFromRequest();
        sysDept.setCreateUserId(tokenData.getUserId());
        sysDept.setCreateUsername(tokenData.getShowName());
        Date now = new Date();
        sysDept.setCreateTime(now);
        sysDept.setUpdateTime(now);
        sysDeptMapper.insert(sysDept);
        return sysDept;
    }

    /**
     * 更新部门对象。
     *
     * @param sysDept         更新的部门对象。
     * @param originalSysDept 原有的部门对象。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysDept sysDept, SysDept originalSysDept) {
        sysDept.setCreateUserId(originalSysDept.getCreateUserId());
        sysDept.setCreateUsername(originalSysDept.getCreateUsername());
        sysDept.setCreateTime(originalSysDept.getCreateTime());
        sysDept.setUpdateTime(new Date());
        sysDept.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        return sysDeptMapper.updateByPrimaryKey(sysDept) != 0;
    }

    /**
     * 删除指定数据。
     *
     * @param deptId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long deptId) {
        Example sysDeptExample = new Example(SysDept.class);
        Example.Criteria c = sysDeptExample.createCriteria();
        c.andEqualTo(super.idFieldName, deptId);
        c.andEqualTo(super.deletedFlagFieldName, GlobalDeletedFlag.NORMAL);
        // 这里先删除主数据
        SysDept deletedObject = new SysDept();
        deletedObject.setDeletedFlag(GlobalDeletedFlag.DELETED);
        if (sysDeptMapper.updateByExampleSelective(deletedObject, sysDeptExample) == 0) {
            return false;
        }
        // 这里可继续删除关联数据。
        SysDataPermDept dataPermDept = new SysDataPermDept();
        dataPermDept.setDeptId(deptId);
        sysDataPermDeptMapper.delete(dataPermDept);
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysDeptListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<SysDept> getSysDeptList(SysDept filter, String orderBy) {
        return sysDeptMapper.getSysDeptList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysDeptList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<SysDept> getSysDeptListWithRelation(SysDept filter, String orderBy) {
        List<SysDept> resultList = sysDeptMapper.getSysDeptList(filter, orderBy);
        Map<String, List<MyWhereCriteria>> criteriaMap = buildAggregationAdditionalWhereCriteria();
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), criteriaMap);
        return resultList;
    }
}
