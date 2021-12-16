package com.orangeforms.webadmin.upms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.hutool.core.util.ObjectUtil;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.RedisKeyUtil;
import com.orangeforms.webadmin.config.ApplicationConfig;
import com.orangeforms.webadmin.upms.service.*;
import com.orangeforms.webadmin.upms.dao.SysPermCodePermMapper;
import com.orangeforms.webadmin.upms.dao.SysPermMapper;
import com.orangeforms.webadmin.upms.model.SysPerm;
import com.orangeforms.webadmin.upms.model.SysPermCodePerm;
import com.orangeforms.webadmin.upms.model.SysPermModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限资源数据服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("sysPermService")
public class SysPermServiceImpl extends BaseService<SysPerm, Long> implements SysPermService {

    @Autowired
    private SysPermMapper sysPermMapper;
    @Autowired
    private SysPermCodePermMapper sysPermCodePermMapper;
    @Autowired
    private SysPermModuleService sysPermModuleService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysPerm> mapper() {
        return sysPermMapper;
    }

    /**
     * 保存新增的权限资源对象。
     *
     * @param perm 新增的权限资源对象。
     * @return 新增后的权限资源对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysPerm saveNew(SysPerm perm) {
        perm.setPermId(idGenerator.nextLongId());
        MyModelUtil.fillCommonsForInsert(perm);
        perm.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        sysPermMapper.insert(perm);
        return perm;
    }

    /**
     * 更新权限资源对象。
     *
     * @param perm         更新的权限资源对象。
     * @param originalPerm 原有的权限资源对象。
     * @return 更新成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysPerm perm, SysPerm originalPerm) {
        MyModelUtil.fillCommonsForUpdate(perm, originalPerm);
        return sysPermMapper.updateById(perm) != 0;
    }

    /**
     * 删除权限资源。
     *
     * @param permId 权限资源主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long permId) {
        if (sysPermMapper.deleteById(permId) != 1) {
            return false;
        }
        SysPermCodePerm permCodePerm = new SysPermCodePerm();
        permCodePerm.setPermId(permId);
        sysPermCodePermMapper.delete(new QueryWrapper<>(permCodePerm));
        return true;
    }

    /**
     * 获取权限数据列表。
     *
     * @param sysPermFilter 过滤对象。
     * @return 权限列表。
     */
    @Override
    public List<SysPerm> getPermListWithRelation(SysPerm sysPermFilter) {
        QueryWrapper<SysPerm> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(this.safeMapToColumnName("showOrder"));
        queryWrapper.eq(ObjectUtil.isNotNull(sysPermFilter.getModuleId()),
        this.safeMapToColumnName("moduleId"), sysPermFilter.getModuleId());
        queryWrapper.like(ObjectUtil.isNotNull(sysPermFilter.getUrl()),
        this.safeMapToColumnName("url"), "%" + sysPermFilter.getUrl() + "%");
        List<SysPerm> permList = sysPermMapper.selectList(queryWrapper);
        // 这里因为权限只有字典数据，所以仅仅做字典关联。
        this.buildRelationForDataList(permList, MyRelationParam.dictOnly());
        return permList;
    }

    /**
     * 将指定用户的指定会话的权限集合存入缓存。
     *
     * @param sessionId 会话Id。
     * @param userId    用户主键Id。
     * @return 查询并缓存后的权限集合。
     */
    @Override
    public Collection<String> putUserSysPermCache(String sessionId, Long userId) {
        Collection<String> permList = this.getPermListByUserId(userId);
        if (CollectionUtils.isEmpty(permList)) {
            return permList;
        }
        String sessionPermKey = RedisKeyUtil.makeSessionPermIdKey(sessionId);
        RSet<String> redisPermSet = redissonClient.getSet(sessionPermKey);
        redisPermSet.addAll(permList.stream().map(Object::toString).collect(Collectors.toSet()));
        redisPermSet.expire(applicationConfig.getSessionExpiredSeconds(), TimeUnit.SECONDS);
        return permList;
    }

    /**
     * 把在线表单的权限URL集合，存放到权限URL的缓存中。
     *
     * @param sessionId  会话Id。
     * @param permUrlSet URL集合。
     */
    @Override
    public void putOnlinePermToCache(String sessionId, Set<String> permUrlSet) {
        String sessionPermKey = RedisKeyUtil.makeSessionPermIdKey(sessionId);
        redissonClient.getSet(sessionPermKey).addAll(permUrlSet);
    }

    /**
     * 将指定会话的权限集合从缓存中移除。
     *
     * @param sessionId 会话Id。
     */
    @Override
    public void removeUserSysPermCache(String sessionId) {
        String sessionPermKey = RedisKeyUtil.makeSessionPermIdKey(sessionId);
        redissonClient.getSet(sessionPermKey).deleteAsync();
    }

    /**
     * 获取与指定用户关联的权限资源列表，已去重。
     *
     * @param userId 关联的用户主键Id。
     * @return 与指定用户Id关联的权限资源列表。
     */
    @Override
    public Collection<String> getPermListByUserId(Long userId) {
        List<String> urlList = sysPermMapper.getPermListByUserId(userId);
        return new HashSet<>(urlList);
    }

    /**
     * 验证权限资源对象关联的数据是否都合法。
     *
     * @param sysPerm         当前操作的对象。
     * @param originalSysPerm 原有对象。
     * @return 验证结果。
     */
    @Override
    public CallResult verifyRelatedData(SysPerm sysPerm, SysPerm originalSysPerm) {
        if (this.needToVerify(sysPerm, originalSysPerm, SysPerm::getModuleId)) {
            SysPermModule permModule = sysPermModuleService.getById(sysPerm.getModuleId());
            if (permModule == null) {
                return CallResult.error("数据验证失败，关联的权限模块Id并不存在，请刷新后重试！");
            }
        }
        return CallResult.ok();
    }

    /**
     * 查询权限资源地址的用户列表。同时返回详细的分配路径。
     *
     * @param permId    权限资源Id。
     * @param loginName 登录名。
     * @return 包含从权限资源到用户的完整权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysUserListWithDetail(Long permId, String loginName) {
        return sysPermMapper.getSysUserListWithDetail(permId, loginName);
    }

    /**
     * 查询权限资源地址的角色列表。同时返回详细的分配路径。
     *
     * @param permId   权限资源Id。
     * @param roleName 角色名。
     * @return 包含从权限资源到角色的权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysRoleListWithDetail(Long permId, String roleName) {
        return sysPermMapper.getSysRoleListWithDetail(permId, roleName);
    }

    /**
     * 查询权限资源地址的菜单列表。同时返回详细的分配路径。
     *
     * @param permId   权限资源Id。
     * @param menuName 菜单名。
     * @return 包含从权限资源到菜单的权限分配路径信息的查询结果列表。
     */
    @Override
    public List<Map<String, Object>> getSysMenuListWithDetail(Long permId, String menuName) {
        return sysPermMapper.getSysMenuListWithDetail(permId, menuName);
    }
}
