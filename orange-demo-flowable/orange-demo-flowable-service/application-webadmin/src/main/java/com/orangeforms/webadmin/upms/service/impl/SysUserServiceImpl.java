package com.orangeforms.webadmin.upms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.alibaba.fastjson.JSONObject;
import com.orangeforms.webadmin.upms.service.*;
import com.orangeforms.webadmin.upms.dao.*;
import com.orangeforms.webadmin.upms.model.*;
import com.orangeforms.webadmin.upms.model.constant.SysUserStatus;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("sysUserService")
public class SysUserServiceImpl extends BaseService<SysUser, Long> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserPostMapper sysUserPostMapper;
    @Autowired
    private SysDataPermUserMapper sysDataPermUserMapper;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysDataPermService sysDataPermService;
    @Autowired
    private SysPostService sysPostService;
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
    @Override
    public SysUser getSysUserByLoginName(String loginName) {
        SysUser filter = new SysUser();
        filter.setLoginName(loginName);
        return sysUserMapper.selectOne(new QueryWrapper<>(filter));
    }

    /**
     * 保存新增的用户对象。
     *
     * @param user          新增的用户对象。
     * @param roleIdSet     用户角色Id集合。
     * @param deptPostIdSet 部门岗位Id集合。
     * @param dataPermIdSet 数据权限Id集合。
     * @return 新增后的用户对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysUser saveNew(SysUser user, Set<Long> roleIdSet, Set<Long> deptPostIdSet, Set<Long> dataPermIdSet) {
        user.setUserId(idGenerator.nextLongId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserStatus(SysUserStatus.STATUS_NORMAL);
        user.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        MyModelUtil.fillCommonsForInsert(user);
        sysUserMapper.insert(user);
        if (CollectionUtils.isNotEmpty(deptPostIdSet)) {
            for (Long deptPostId : deptPostIdSet) {
                SysDeptPost deptPost = sysDeptService.getSysDeptPost(deptPostId);
                SysUserPost userPost = new SysUserPost();
                userPost.setUserId(user.getUserId());
                userPost.setDeptPostId(deptPostId);
                userPost.setPostId(deptPost.getPostId());
                sysUserPostMapper.insert(userPost);
            }
        }
        if (CollectionUtils.isNotEmpty(roleIdSet)) {
            for (Long roleId : roleIdSet) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
        if (CollectionUtils.isNotEmpty(dataPermIdSet)) {
            for (Long dataPermId : dataPermIdSet) {
                SysDataPermUser dataPermUser = new SysDataPermUser();
                dataPermUser.setDataPermId(dataPermId);
                dataPermUser.setUserId(user.getUserId());
                sysDataPermUserMapper.insert(dataPermUser);
            }
        }
        return user;
    }

    /**
     * 更新用户对象。
     *
     * @param user          更新的用户对象。
     * @param originalUser  原有的用户对象。
     * @param roleIdSet     用户角色Id列表。
     * @param deptPostIdSet 部门岗位Id集合。
     * @param dataPermIdSet 数据权限Id集合。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysUser user, SysUser originalUser, Set<Long> roleIdSet, Set<Long> deptPostIdSet, Set<Long> dataPermIdSet) {
        user.setLoginName(originalUser.getLoginName());
        user.setPassword(originalUser.getPassword());
        MyModelUtil.fillCommonsForUpdate(user, originalUser);
        UpdateWrapper<SysUser> uw = this.createUpdateQueryForNullValue(user, user.getUserId());
        if (sysUserMapper.update(user, uw) != 1) {
            return false;
        }
        // 先删除原有的User-Post关联关系，再重新插入新的关联关系
        SysUserPost deletedUserPost = new SysUserPost();
        deletedUserPost.setUserId(user.getUserId());
        sysUserPostMapper.delete(new QueryWrapper<>(deletedUserPost));
        if (CollectionUtils.isNotEmpty(deptPostIdSet)) {
            for (Long deptPostId : deptPostIdSet) {
                SysDeptPost deptPost = sysDeptService.getSysDeptPost(deptPostId);
                SysUserPost userPost = new SysUserPost();
                userPost.setUserId(user.getUserId());
                userPost.setDeptPostId(deptPostId);
                userPost.setPostId(deptPost.getPostId());
                sysUserPostMapper.insert(userPost);
            }
        }
        // 先删除原有的User-Role关联关系，再重新插入新的关联关系
        SysUserRole deletedUserRole = new SysUserRole();
        deletedUserRole.setUserId(user.getUserId());
        sysUserRoleMapper.delete(new QueryWrapper<>(deletedUserRole));
        if (CollectionUtils.isNotEmpty(roleIdSet)) {
            for (Long roleId : roleIdSet) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
        // 先删除原有的DataPerm-User关联关系，在重新插入新的关联关系
        SysDataPermUser deletedDataPermUser = new SysDataPermUser();
        deletedDataPermUser.setUserId(user.getUserId());
        sysDataPermUserMapper.delete(new QueryWrapper<>(deletedDataPermUser));
        if (CollectionUtils.isNotEmpty(dataPermIdSet)) {
            for (Long dataPermId : dataPermIdSet) {
                SysDataPermUser dataPermUser = new SysDataPermUser();
                dataPermUser.setDataPermId(dataPermId);
                dataPermUser.setUserId(user.getUserId());
                sysDataPermUserMapper.insert(dataPermUser);
            }
        }
        return true;
    }

    /**
     * 修改用户密码。
     * @param userId  用户主键Id。
     * @param newPass 新密码。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changePassword(Long userId, String newPass) {
        SysUser updatedUser = new SysUser();
        updatedUser.setUserId(userId);
        updatedUser.setPassword(passwordEncoder.encode(newPass));
        return sysUserMapper.updateById(updatedUser) == 1;
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
        if (sysUserMapper.deleteById(userId) == 0) {
            return false;
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        sysUserRoleMapper.delete(new QueryWrapper<>(userRole));
        SysDataPermUser dataPermUser = new SysDataPermUser();
        dataPermUser.setUserId(userId);
        sysDataPermUserMapper.delete(new QueryWrapper<>(dataPermUser));
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
    @Override
    public List<SysUser> getSysUserList(SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getSysUserList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<SysUser> getSysUserListWithRelation(SysUser filter, String orderBy) {
        List<SysUser> resultList = sysUserMapper.getSysUserList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public List<SysUser> getNotInSysUserListByDataPermId(Long dataPermId, SysUser filter, String orderBy) {
        return sysUserMapper.getNotInSysUserListByDataPermId(dataPermId, filter, orderBy);
    }

    @Override
    public List<SysUser> getSysUserListByDeptPostId(Long deptPostId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByDeptPostId(deptPostId, filter, orderBy);
    }

    @Override
    public List<SysUser> getNotInSysUserListByDeptPostId(Long deptPostId, SysUser filter, String orderBy) {
        return sysUserMapper.getNotInSysUserListByDeptPostId(deptPostId, filter, orderBy);
    }

    @Override
    public List<SysUser> getSysUserListByPostId(Long postId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByPostId(postId, filter, orderBy);
    }

    /**
     * 查询用户的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param userId 用户Id。
     * @param url    url过滤条件。
     * @return 包含从用户到权限资源的完整权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysPermListWithDetail(Long userId, String url) {
        return sysUserMapper.getSysPermListWithDetail(userId, url);
    }

    /**
     * 查询用户的权限字列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param permCode 权限字名称过滤条件。
     * @return 包含从用户到权限字的权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysPermCodeListWithDetail(Long userId, String permCode) {
        return sysUserMapper.getSysPermCodeListWithDetail(userId, permCode);
    }

    /**
     * 查询用户的菜单列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param menuName 菜单名称过滤条件。
     * @return 包含从用户到菜单的权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysMenuListWithDetail(Long userId, String menuName) {
        return sysUserMapper.getSysMenuListWithDetail(userId, menuName);
    }

    /**
     * 验证用户对象关联的数据是否都合法。
     *
     * @param sysUser         当前操作的对象。
     * @param originalSysUser 原有对象。
     * @param roleIds         逗号分隔的角色Id列表字符串。
     * @param deptPostIds     逗号分隔的部门岗位Id列表字符串。
     * @param dataPermIds     逗号分隔的数据权限Id列表字符串。
     * @return 验证结果。
     */
    @Override
    public CallResult verifyRelatedData(
            SysUser sysUser, SysUser originalSysUser, String roleIds, String deptPostIds, String dataPermIds) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(deptPostIds)) {
            return CallResult.error("数据验证失败，用户的部门岗位数据不能为空！");
        }
        Set<Long> deptPostIdSet =
                Arrays.stream(deptPostIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysPostService.existAllPrimaryKeys(deptPostIdSet, sysUser.getDeptId())) {
            return CallResult.error("数据验证失败，存在不合法的用户岗位，请刷新后重试！");
        }
        jsonObject.put("deptPostIdSet", deptPostIdSet);
        if (StringUtils.isBlank(roleIds)) {
            return CallResult.error("数据验证失败，用户的角色数据不能为空！");
        }
        Set<Long> roleIdSet = Arrays.stream(
                roleIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysRoleService.existAllPrimaryKeys(roleIdSet)) {
            return CallResult.error("数据验证失败，存在不合法的用户角色，请刷新后重试！");
        }
        jsonObject.put("roleIdSet", roleIdSet);
        if (StringUtils.isBlank(dataPermIds)) {
            return CallResult.error("数据验证失败，用户的数据权限不能为空！");
        }
        Set<Long> dataPermIdSet = Arrays.stream(
                dataPermIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysDataPermService.existAllPrimaryKeys(dataPermIdSet)) {
            return CallResult.error("数据验证失败，存在不合法的数据权限，请刷新后重试！");
        }
        jsonObject.put("dataPermIdSet", dataPermIdSet);
        //这里是基于字典的验证。
        if (this.needToVerify(sysUser, originalSysUser, SysUser::getDeptId)
                && !sysDeptService.existId(sysUser.getDeptId())) {
            return CallResult.error("数据验证失败，关联的用户部门Id并不存在，请刷新后重试！");
        }
        return CallResult.ok(jsonObject);
    }
}
