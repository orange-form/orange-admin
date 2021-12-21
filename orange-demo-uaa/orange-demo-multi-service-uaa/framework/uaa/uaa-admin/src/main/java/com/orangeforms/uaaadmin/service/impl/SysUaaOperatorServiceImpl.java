package com.orangeforms.uaaadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.uaaadmin.service.SysUaaOperatorService;
import com.orangeforms.uaaadmin.dao.SysUaaOperatorMapper;
import com.orangeforms.uaaadmin.model.SysUaaOperator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 操作员数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service("sysUaaOperatorService")
public class SysUaaOperatorServiceImpl extends BaseService<SysUaaOperator, Long> implements SysUaaOperatorService {

    @Autowired
    private SysUaaOperatorMapper sysUaaOperatorMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysUaaOperator> mapper() {
        return sysUaaOperatorMapper;
    }

    /**
     * 根据登录名获取UAA操作员对象。
     * @param loginName UAA操作员登录名。
     * @return UAA操作员对象。
     */
    @Override
    public SysUaaOperator getUaaOperatorByLoginName(String loginName) {
        SysUaaOperator sysUaaOperator = new SysUaaOperator();
        sysUaaOperator.setLoginName(loginName);
        return sysUaaOperatorMapper.selectOne(new QueryWrapper<>(sysUaaOperator));
    }

    /**
     * 保存新增对象。
     *
     * @param sysUaaOperator 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysUaaOperator saveNew(SysUaaOperator sysUaaOperator) {
        sysUaaOperator.setPassword(passwordEncoder.encode(sysUaaOperator.getPassword()));
        sysUaaOperator.setOperatorId(idGenerator.nextLongId());
        MyModelUtil.fillCommonsForInsert(sysUaaOperator);
        sysUaaOperatorMapper.insert(sysUaaOperator);
        return sysUaaOperator;
    }

    /**
     * 更新数据对象。
     *
     * @param sysUaaOperator         更新的对象。
     * @param originalSysUaaOperator 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysUaaOperator sysUaaOperator, SysUaaOperator originalSysUaaOperator) {
        sysUaaOperator.setPassword(originalSysUaaOperator.getPassword());
        MyModelUtil.fillCommonsForUpdate(sysUaaOperator, originalSysUaaOperator);
        return sysUaaOperatorMapper.updateById(sysUaaOperator) == 1;
    }

    /**
     * 修改密码。
     *
     * @param uaaOperatorId 操作员Id。
     * @param newPass       新密码明文。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changePassword(Long uaaOperatorId, String newPass) {
        SysUaaOperator updatedOperator = new SysUaaOperator();
        updatedOperator.setOperatorId(uaaOperatorId);
        updatedOperator.setPassword(passwordEncoder.encode(newPass));
        return sysUaaOperatorMapper.updateById(updatedOperator) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param operatorId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long operatorId) {
        return sysUaaOperatorMapper.deleteById(operatorId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysUaaOperatorListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysUaaOperator> getSysUaaOperatorList(SysUaaOperator filter, String orderBy) {
        return sysUaaOperatorMapper.getSysUaaOperatorList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysUaaOperatorList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysUaaOperator> getSysUaaOperatorListWithRelation(SysUaaOperator filter, String orderBy) {
        List<SysUaaOperator> resultList = sysUaaOperatorMapper.getSysUaaOperatorList(filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.normal());
        return resultList;
    }
}
