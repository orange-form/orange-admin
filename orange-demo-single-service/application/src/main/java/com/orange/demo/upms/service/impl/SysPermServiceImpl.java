package com.orange.demo.upms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.object.MyRelationParam;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.common.core.object.CallResult;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.util.RedisKeyUtil;
import com.orange.demo.config.ApplicationConfig;
import com.orange.demo.upms.service.*;
import com.orange.demo.upms.dao.SysPermCodePermMapper;
import com.orange.demo.upms.dao.SysPermMapper;
import com.orange.demo.upms.model.SysPerm;
import com.orange.demo.upms.model.SysPermCodePerm;
import com.orange.demo.upms.model.SysPermModule;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 权限资源数据服务类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
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
    private JedisPool jedisPool;
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
        perm.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        MyModelUtil.fillCommonsForUpdate(perm, originalPerm);
        return sysPermMapper.updateByPrimaryKeySelective(perm) != 0;
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
        if (!this.removeById(permId)) {
            return false;
        }
        SysPermCodePerm permCodePerm = new SysPermCodePerm();
        permCodePerm.setPermId(permId);
        sysPermCodePermMapper.delete(permCodePerm);
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
        Example e = new Example(SysPerm.class);
        e.orderBy("showOrder");
        Example.Criteria c = e.createCriteria();
        if (ObjectUtil.isNotNull(sysPermFilter.getModuleId())) {
            c.andEqualTo("moduleId", sysPermFilter.getModuleId());
        }
        if (ObjectUtil.isNotNull(sysPermFilter.getUrl())) {
            c.andLike("url", "%" + sysPermFilter.getUrl() + "%");
        }
        c.andEqualTo("deletedFlag", GlobalDeletedFlag.NORMAL);
        List<SysPerm> permList = sysPermMapper.selectByExample(e);
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
        String sessionPermKey = RedisKeyUtil.makeSessionPermIdKeyForRedis(sessionId);
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction t = jedis.multi();
            if (permList != null) {
                for (String perm : permList) {
                    t.sadd(sessionPermKey, perm);
                }
                t.expire(sessionPermKey, applicationConfig.getPermRedisExpiredSeconds());
            }
            t.exec();
        }
        return permList;
    }

    /**
     * 将指定会话的权限集合从缓存中移除。
     *
     * @param sessionId 会话Id。
     */
    @Override
    public void removeUserSysPermCache(String sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String sessionPermKey = RedisKeyUtil.makeSessionPermIdKeyForRedis(sessionId);
            jedis.del(sessionPermKey);
        }
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
