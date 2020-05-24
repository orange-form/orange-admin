package com.orange.admin.upms.service;

import com.alibaba.fastjson.JSONObject;
import com.orange.admin.upms.dao.*;
import com.orange.admin.upms.model.*;
import com.orange.admin.upms.model.constant.SysUserStatus;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.constant.GlobalDeletedFlag;
import com.orange.admin.common.core.object.TokenData;
import com.orange.admin.common.core.object.MyWhereCriteria;
import com.orange.admin.common.core.object.MyRelationParam;
import com.orange.admin.common.core.object.CallResult;
import com.orange.admin.common.core.util.MyCommonUtil;
import com.orange.admin.common.biz.base.service.BaseBizService;
import com.orange.admin.common.biz.util.BasicIdGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理数据操作服务类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Service
public class SysUserService extends BaseBizService<SysUser, Long> {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysDataPermUserMapper sysDataPermUserMapper;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysDataPermService sysDataPermService;
    @Autowired
    private BasicIdGenerator idGenerator;

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
     * @param user          新增的用户对象。
     * @param roleIdSet     用户角色Id集合。
     * @param dataPermIdSet 数据权限Id集合。
     * @param passwdSalt    密码的盐。
     * @return 新增后的用户对象。
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUser saveNew(SysUser user, Set<Long> roleIdSet, Set<Long> dataPermIdSet, String passwdSalt) {
        user.setUserId(idGenerator.nextLongId());
        user.setPassword(MyCommonUtil.encrptedPassword(user.getPassword(), passwdSalt));
        user.setUserStatus(SysUserStatus.STATUS_NORMAL);
        user.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        TokenData tokenData = TokenData.takeFromRequest();
        user.setCreateUserId(tokenData.getUserId());
        user.setCreateUsername(tokenData.getShowName());
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        sysUserMapper.insert(user);
        if (CollectionUtils.isNotEmpty(roleIdSet)) {
            List<SysUserRole> userRoleList = new LinkedList<>();
            for (Long roleId : roleIdSet) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(roleId);
                userRoleList.add(userRole);
            }
            sysUserRoleMapper.insertList(userRoleList);
        }
        if (CollectionUtils.isNotEmpty(dataPermIdSet)) {
            List<SysDataPermUser> dataPermUserList = new LinkedList<>();
            for (Long dataPermId : dataPermIdSet) {
                SysDataPermUser dataPermUser = new SysDataPermUser();
                dataPermUser.setDataPermId(dataPermId);
                dataPermUser.setUserId(user.getUserId());
                dataPermUserList.add(dataPermUser);
            }
            sysDataPermUserMapper.insertList(dataPermUserList);
        }
        return user;
    }

    /**
     * 更新用户对象。
     *
     * @param user          更新的用户对象。
     * @param originalUser  原有的用户对象。
     * @param roleIdSet     用户角色Id列表。
     * @param dataPermIdSet 数据权限Id集合。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysUser user, SysUser originalUser, Set<Long> roleIdSet, Set<Long> dataPermIdSet) {
        user.setLoginName(originalUser.getLoginName());
        user.setPassword(originalUser.getPassword());
        user.setCreateUserId(originalUser.getCreateUserId());
        user.setCreateUsername(originalUser.getCreateUsername());
        user.setCreateTime(originalUser.getCreateTime());
        user.setUpdateTime(new Date());
        if (sysUserMapper.updateByPrimaryKeySelective(user) != 1) {
            return false;
        }
        // 先删除原有的User-Role关联关系，再重新插入新的关联关系
        SysUserRole deletedUserRole = new SysUserRole();
        deletedUserRole.setUserId(user.getUserId());
        sysUserRoleMapper.delete(deletedUserRole);
        if (CollectionUtils.isNotEmpty(roleIdSet)) {
            List<SysUserRole> userRoleList = new LinkedList<>();
            for (Long roleId : roleIdSet) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(roleId);
                userRoleList.add(userRole);
            }
            sysUserRoleMapper.insertList(userRoleList);
        }
        // 先删除原有的DataPerm-User关联关系，在重新插入新的关联关系
        SysDataPermUser deletedDataPermUser = new SysDataPermUser();
        deletedDataPermUser.setUserId(user.getUserId());
        sysDataPermUserMapper.delete(deletedDataPermUser);
        if (CollectionUtils.isNotEmpty(dataPermIdSet)) {
            List<SysDataPermUser> dataPermUserList = new LinkedList<>();
            for (Long dataPermId : dataPermIdSet) {
                SysDataPermUser dataPermUser = new SysDataPermUser();
                dataPermUser.setDataPermId(dataPermId);
                dataPermUser.setUserId(user.getUserId());
                dataPermUserList.add(dataPermUser);
            }
            sysDataPermUserMapper.insertList(dataPermUserList);
        }
        return true;
    }

    /**
     * 重置用户密码。
     * @param userId        用户主键Id。
     * @param defaultPasswd 缺省密码。
     * @param passwdSalt    密码的盐。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String defaultPasswd, String passwdSalt) {
        Example e = new Example(SysUser.class);
        e.createCriteria().andEqualTo(super.idFieldName, userId);
        e.createCriteria().andEqualTo(super.deletedFlagFieldName, GlobalDeletedFlag.NORMAL);
        SysUser updatedUser = new SysUser();
        updatedUser.setPassword(MyCommonUtil.encrptedPassword(defaultPasswd, passwdSalt));
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
        if (sysUserMapper.updateByExampleSelective(deletedObject, sysUserExample) == 0) {
            return false;
        }
        // 这里可继续删除关联数据。
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        sysUserRoleMapper.delete(userRole);
        SysDataPermUser dataPermUser = new SysDataPermUser();
        dataPermUser.setUserId(userId);
        sysDataPermUserMapper.delete(dataPermUser);
        return true;
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
        return sysUserMapper.getSysUserList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 如果仅仅需要获取主表数据，请移步(getSysUserList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    public List<SysUser> getSysUserListWithRelation(SysUser filter, String orderBy) {
        List<SysUser> resultList = sysUserMapper.getSysUserList(filter, orderBy);
        Map<String, List<MyWhereCriteria>> criteriaMap = buildAggregationAdditionalWhereCriteria();
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), criteriaMap);
        return resultList;
    }

    /**
     * 获取指定角色的用户列表。
     *
     * @param roleId  角色主键Id。
     * @param filter  用户过滤对象。
     * @param orderBy 排序参数。
     * @return 用户列表。
     */
    public List<SysUser> getSysUserListByRoleId(Long roleId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByRoleId(roleId, filter, orderBy);
    }

    /**
     * 获取不属于指定角色的用户列表。
     *
     * @param roleId  角色主键Id。
     * @param filter  用户过滤对象。
     * @param orderBy 排序参数。
     * @return 用户列表。
     */
    public List<SysUser> getNotInSysUserListByRoleId(Long roleId, SysUser filter, String orderBy) {
        return sysUserMapper.getNotInSysUserListByRoleId(roleId, filter, orderBy);
    }

    /**
     * 获取指定数据权限的用户列表。
     *
     * @param dataPermId 数据权限主键Id。
     * @param filter     用户过滤对象。
     * @param orderBy    排序参数。
     * @return 用户列表。
     */
    public List<SysUser> getSysUserListByDataPermId(Long dataPermId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByDataPermId(dataPermId, filter, orderBy);
    }

    /**
     * 获取不属于指定数据权限的用户列表。
     *
     * @param dataPermId 数据权限主键Id。
     * @param filter     用户过滤对象。
     * @param orderBy    排序参数。
     * @return 用户列表。
     */
    public List<SysUser> getNotInSysUserListByDataPermId(Long dataPermId, SysUser filter, String orderBy) {
        return sysUserMapper.getNotInSysUserListByDataPermId(dataPermId, filter, orderBy);
    }

    /**
     * 验证用户对象关联的数据是否都合法。
     *
     * @param sysUser              当前操作的对象。
     * @param originalSysUser      原有对象。
     * @param roleIdListString     逗号分隔的角色Id列表字符串。
     * @param dataPermIdListString 逗号分隔的数据权限Id列表字符串。
     * @return 验证结果。
     */
    public CallResult verifyRelatedData(
            SysUser sysUser, SysUser originalSysUser, String roleIdListString, String dataPermIdListString) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(roleIdListString)) {
            return CallResult.error("数据验证失败，用户的角色数据不能为空！");
        }
        Set<Long> roleIdSet = Arrays.stream(
                roleIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysRoleService.existAllPrimaryKeys(roleIdSet)) {
            return CallResult.error("数据验证失败，存在不合法的用户角色，请刷新后重试！");
        }
        jsonObject.put("roleIdSet", roleIdSet);
        if (StringUtils.isBlank(dataPermIdListString)) {
            return CallResult.error("数据验证失败，用户的数据权限不能为空！");
        }
        Set<Long> dataPermIdSet = Arrays.stream(
                dataPermIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysDataPermService.existAllPrimaryKeys(dataPermIdSet)) {
            return CallResult.error("数据验证失败，存在不合法的数据权限，请刷新后重试！");
        }
        jsonObject.put("dataPermIdSet", dataPermIdSet);
        return CallResult.ok(jsonObject);
    }
}
