package com.orangeforms.uaaadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.uaaadmin.service.SysUaaUserService;
import com.orangeforms.uaaadmin.dao.SysUaaUserMapper;
import com.orangeforms.uaaadmin.model.SysUaaUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * UAA用户数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service("sysUaaUserService")
public class SysUaaUserServiceImpl extends BaseService<SysUaaUser, Long> implements SysUaaUserService {

    @Autowired
    private SysUaaUserMapper sysUaaUserMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysUaaUser> mapper() {
        return sysUaaUserMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param sysUaaUser 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysUaaUser saveNew(SysUaaUser sysUaaUser) {
        sysUaaUser.setUserId(idGenerator.nextLongId());
        sysUaaUser.setPassword(passwordEncoder.encode(sysUaaUser.getPassword()));
        MyModelUtil.fillCommonsForInsert(sysUaaUser);
        sysUaaUserMapper.insert(sysUaaUser);
        return sysUaaUser;
    }

    /**
     * 更新数据对象。
     *
     * @param sysUaaUser         更新的对象。
     * @param originalSysUaaUser 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysUaaUser sysUaaUser, SysUaaUser originalSysUaaUser) {
        sysUaaUser.setPassword(originalSysUaaUser.getPassword());
        MyModelUtil.fillCommonsForUpdate(sysUaaUser, originalSysUaaUser);
        return sysUaaUserMapper.updateById(sysUaaUser) == 1;
    }

    /**
     * 修改密码。
     *
     * @param uaaUserId  用户Id。
     * @param newPass    新密码明文。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changePassword(Long uaaUserId, String newPass) {
        SysUaaUser updatedUser = new SysUaaUser();
        updatedUser.setUserId(uaaUserId);
        updatedUser.setPassword(passwordEncoder.encode(newPass));
        return sysUaaUserMapper.updateById(updatedUser) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param userId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long userId) {
        return sysUaaUserMapper.deleteById(userId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysUaaUserListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysUaaUser> getSysUaaUserList(SysUaaUser filter, String orderBy) {
        return sysUaaUserMapper.getSysUaaUserList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysUaaUserList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysUaaUser> getSysUaaUserListWithRelation(SysUaaUser filter, String orderBy) {
        List<SysUaaUser> resultList = sysUaaUserMapper.getSysUaaUserList(filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.normal());
        return resultList;
    }
}
