package com.orange.admin.upms.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.orange.admin.common.biz.base.service.BaseBizService;
import com.orange.admin.common.biz.util.BasicIdGenerator;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.object.MyRelationParam;
import com.orange.admin.common.core.constant.GlobalDeletedFlag;
import com.orange.admin.common.core.object.CallResult;
import com.orange.admin.upms.dao.SysPermCodePermMapper;
import com.orange.admin.upms.dao.SysPermMapper;
import com.orange.admin.upms.model.SysPerm;
import com.orange.admin.upms.model.SysPermCodePerm;
import com.orange.admin.upms.model.SysPermModule;
import com.orange.admin.upms.model.SysUser;
import com.orange.admin.upms.model.constant.SysUserType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限资源数据服务类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Service
public class SysPermService extends BaseBizService<SysPerm, Long> {

    @Autowired
    private SysPermMapper sysPermMapper;
    @Autowired
    private SysPermCodePermMapper sysPermCodePermMapper;
    @Autowired
    private SysPermModuleService sysPermModuleService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private BasicIdGenerator idGenerator;

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
    public SysPerm saveNew(SysPerm perm) {
        perm.setPermId(idGenerator.nextLongId());
        perm.setCreateTime(new Date());
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
    public boolean update(SysPerm perm, SysPerm originalPerm) {
        perm.setCreateTime(originalPerm.getCreateTime());
        perm.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        return sysPermMapper.updateByPrimaryKeySelective(perm) != 0;
    }

    /**
     * 删除权限资源。
     *
     * @param permId 权限资源主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long permId) {
        SysPerm perm = new SysPerm();
        perm.setPermId(permId);
        perm.setDeletedFlag(GlobalDeletedFlag.DELETED);
        if (sysPermMapper.updateByPrimaryKeySelective(perm) != 1) {
            return false;
        }
        Example e = new Example(SysPermCodePerm.class);
        e.createCriteria().andEqualTo("permId", permId);
        sysPermCodePermMapper.deleteByExample(e);
        return true;
    }

    /**
     * 获取权限数据列表。
     *
     * @param sysPermFilter 过滤对象。
     * @return 权限列表。
     */
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
        this.buildRelationForDataList(permList, MyRelationParam.dictOnly(), null);
        return permList;
    }

    /**
     * 获取指定用户的权限资源集合，并存储于缓存，从而提升后续读取效率。
     *
     * @param sessionId 用户会话Id。
     * @param userId    用户主键Id。
     * @return 当前用户权限集合。
     */
    @Cacheable(value = "USER_PERMISSION_CACHE", key = "#sessionId", unless = "#result == null")
    public Set<String> getCacheableSysPermSetByUserId(String sessionId, Long userId) {
        // 这里可以防止非法的userId直接访问权限受限的url
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return new HashSet<>(1);
        }
        // 管理员账户返回空对象，便于缓存的统一处理。
        return user.getUserType() == SysUserType.TYPE_ADMIN
                ? new HashSet<>(1) : this.getSysPermSetByUserId(userId);
    }

    /**
     * 将指定用户的指定会话的权限集合存入缓存。
     *
     * @param sessionId 会话Id。
     * @param userId    用户主键Id。
     * @param isAdmin   是否是管理员。
     * @return 查询并缓存后的权限集合。
     */
    @CachePut(value = "USER_PERMISSION_CACHE", key = "#sessionId")
    public Set<String> putUserSysPermCache(String sessionId, Long userId, boolean isAdmin) {
        // 管理员账户返回空对象，便于缓存的统一处理。
        return isAdmin ? new HashSet<>(1) : this.getSysPermSetByUserId(userId);
    }

    /**
     * 将指定会话的权限集合从缓存中移除。
     *
     * @param sessionId 会话Id。
     */
    @CacheEvict(value = "USER_PERMISSION_CACHE", key = "#sessionId")
    public void removeUserSysPermCache(String sessionId) {
        // 空实现即可，只是通过注解将当前sessionId从cache中删除。
    }

    /**
     * 获取指定用户的权限集合，这里之所以为公有方法，因为spring cache的技术要求，私有方法数据不能缓存。
     *
     * @param userId 用户主键Id。
     * @return 用户权限集合。
     */
    public Set<String> getSysPermSetByUserId(Long userId) {
        List<SysPerm> permList = this.getPermListByUserId(userId);
        return permList.stream().map(SysPerm::getUrl).collect(Collectors.toSet());
    }

    /**
     * 获取与指定权限字关联的权限资源列表。
     *
     * @param permCodeId 关联的权限字主键Id。
     * @param orderBy    排序参数。
     * @return 与指定权限字Id关联的权限资源列表。
     */
    public List<SysPerm> getPermListByPermCodeId(Long permCodeId, String orderBy) {
        return sysPermMapper.getPermListByPermCodeId(permCodeId, orderBy);
    }

    /**
     * 获取与指定用户关联的权限资源列表。
     *
     * @param userId 关联的用户主键Id。
     * @return 与指定用户Id关联的权限资源列表。
     */
    public List<SysPerm> getPermListByUserId(Long userId) {
        return sysPermMapper.getPermListByUserId(userId);
    }

    /**
     * 获取指定用户的用户权限关联列表。
     *
     * @param loginName 精确匹配用户登录名。
     * @param moduleId  精确匹配权限模块Id。
     * @param url       模糊匹配的url过滤条件。
     * @return 用户权限关联列表。
     */
    public List<Map<String, Object>> getUserPermListByFilter(String loginName, Long moduleId, String url) {
        return sysPermMapper.getUserPermListByFilter(loginName, moduleId, url);
    }

    /**
     * 获取指定权限资源的权限用户关联数据列表。
     *
     * @param permId 权限资源主键Id。
     * @return 用户和权限资源关联列表。
     */
    public List<Map<String, Object>> getPermUserListById(Long permId) {
        return sysPermMapper.getPermUserListById(permId);
    }

    /**
     * 获取指定权限资源的权限角色关联数据列表。
     *
     * @param permId 权限资源主键Id。
     * @return 角色和权限资源关联列表。
     */
    public List<Map<String, Object>> getPermRoleListById(Long permId) {
        return sysPermMapper.getPermRoleListById(permId);
    }

    /**
     * 验证权限资源对象关联的数据是否都合法。
     *
     * @param sysPerm         当前操作的对象。
     * @param originalSysPerm 原有对象。
     * @return 验证结果。
     */
    public CallResult verifyRelatedData(SysPerm sysPerm, SysPerm originalSysPerm) {
        JSONObject jsonObject = null;
        if (this.needToVerify(sysPerm, originalSysPerm, SysPerm::getModuleId)) {
            SysPermModule permModule = sysPermModuleService.getById(sysPerm.getModuleId());
            if (permModule == null) {
                return CallResult.error("数据验证失败，关联的权限模块Id并不存在，请刷新后重试！");
            }
            jsonObject = new JSONObject();
            jsonObject.put("permModule", permModule);
        }
        return CallResult.ok(jsonObject);
    }
}
