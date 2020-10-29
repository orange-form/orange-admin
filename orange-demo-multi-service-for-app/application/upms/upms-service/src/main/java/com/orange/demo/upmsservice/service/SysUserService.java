package com.orange.demo.upmsservice.service;

import com.alibaba.fastjson.JSONObject;
import com.orange.demo.upmsservice.dao.*;
import com.orange.demo.upmsservice.model.*;
import com.orange.demo.upmsinterface.dto.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.orange.demo.upmsinterface.constant.SysUserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 用户管理数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service
public class SysUserService extends BaseService<SysUser, SysUserDto, Long> {

    @Autowired
    private SysUserMapper sysUserMapper;
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
    protected BaseDaoMapper<SysUser> mapper() {
        return sysUserMapper;
    }

    /**
     * 获取指定登录名的用户对象。
     *
     * @param loginName 指定登录用户名。
     * @return 用户对象。
     */
    public SysUser getSysUserByLoginName(String loginName) {
        Example e = new Example(SysUser.class);
        Example.Criteria c = e.createCriteria();
        c.andEqualTo("loginName", loginName);
        c.andEqualTo("deletedFlag", GlobalDeletedFlag.NORMAL);
        return sysUserMapper.selectOneByExample(e);
    }

    /**
     * 保存新增的用户对象。
     *
     * @param user 新增的用户对象。
     * @return 新增后的用户对象。
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUser saveNew(SysUser user) {
        user.setUserId(idGenerator.nextLongId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserStatus(SysUserStatus.STATUS_NORMAL);
        user.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        TokenData tokenData = TokenData.takeFromRequest();
        user.setCreateUserId(tokenData.getUserId());
        user.setCreateUsername(tokenData.getShowName());
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        sysUserMapper.insert(user);
        return user;
    }

    /**
     * 更新用户对象。
     *
     * @param user         更新的用户对象。
     * @param originalUser 原有的用户对象。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysUser user, SysUser originalUser) {
        user.setLoginName(originalUser.getLoginName());
        user.setPassword(originalUser.getPassword());
        user.setCreateUserId(originalUser.getCreateUserId());
        user.setCreateUsername(originalUser.getCreateUsername());
        user.setCreateTime(originalUser.getCreateTime());
        user.setUpdateTime(new Date());
        return sysUserMapper.updateByPrimaryKeySelective(user) == 1;
    }

    /**
     * 重置用户密码。
     * @param userId  用户主键Id。
     * @param newPass 新密码。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String newPass) {
        Example e = new Example(SysUser.class);
        e.createCriteria().andEqualTo(super.idFieldName, userId);
        e.createCriteria().andEqualTo(super.deletedFlagFieldName, GlobalDeletedFlag.NORMAL);
        SysUser updatedUser = new SysUser();
        updatedUser.setPassword(passwordEncoder.encode(newPass));
        return sysUserMapper.updateByExampleSelective(updatedUser, e) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param userId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long userId) {
        Example sysUserExample = new Example(SysUser.class);
        Example.Criteria c = sysUserExample.createCriteria();
        c.andEqualTo(super.idFieldName, userId);
        c.andEqualTo(super.deletedFlagFieldName, GlobalDeletedFlag.NORMAL);
        // 这里先删除主数据
        SysUser deletedObject = new SysUser();
        deletedObject.setDeletedFlag(GlobalDeletedFlag.DELETED);
        return sysUserMapper.updateByExampleSelective(deletedObject, sysUserExample) != 0;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysUserListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<SysUser> getSysUserList(SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserList(null, null, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getSysUserListWithRelation)方法。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public <M> List<SysUser> getSysUserList(
            String inFilterField, Set<M> inFilterValues, SysUser filter, String orderBy) {
        String inFilterColumn = MyModelUtil.mapToColumnName(inFilterField, SysUser.class);
        return sysUserMapper.getSysUserList(inFilterColumn, inFilterValues, filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysUserList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    public List<SysUser> getSysUserListWithRelation(SysUser filter, String orderBy) {
        List<SysUser> resultList = sysUserMapper.getSysUserList(null, null, filter, orderBy);
        Map<String, List<MyWhereCriteria>> criteriaMap = buildAggregationAdditionalWhereCriteria();
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), criteriaMap);
        return resultList;
    }

    /**
     * 获取主表的查询结果，查询条件中包括主表过滤对象和指定字段的(in list)过滤。
     * 同时还包含主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysUserList)，以便获取更好的查询性能。
     *
     * @param inFilterField (In-list)指定的字段(Java成员字段，而非数据列名)。
     * @param inFilterValues inFilterField指定字段的(In-list)数据列表。
     * @param filter 主表过滤对象。
     * @param orderBy 排序对象。
     * @return 查询结果集。
     */
    public <M> List<SysUser> getSysUserListWithRelation(
            String inFilterField, Set<M> inFilterValues, SysUser filter, String orderBy) {
        List<SysUser> resultList =
                sysUserMapper.getSysUserList(inFilterField, inFilterValues, filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly(), null);
        return resultList;
    }

    /**
     * 验证用户对象关联的数据是否都合法。
     *
     * @param sysUser         当前操作的对象。
     * @param originalSysUser 原有对象。
     * @return 验证结果。
     */
    public CallResult verifyRelatedData(SysUser sysUser, SysUser originalSysUser) {
        JSONObject jsonObject = new JSONObject();
        return CallResult.ok(jsonObject);
    }
}
