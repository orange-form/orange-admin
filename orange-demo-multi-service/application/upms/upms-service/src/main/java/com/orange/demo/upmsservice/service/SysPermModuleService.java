package com.orange.demo.upmsservice.service;

import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.sequence.wrapper.IdGeneratorWrapper;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.common.core.constant.GlobalDeletedFlag;
import com.orange.demo.upmsinterface.dto.SysPermModuleDto;
import com.orange.demo.upmsservice.dao.SysPermModuleMapper;
import com.orange.demo.upmsservice.model.SysPerm;
import com.orange.demo.upmsservice.model.SysPermModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 权限资源模块数据服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service
public class SysPermModuleService extends BaseService<SysPermModule, SysPermModuleDto, Long> {

    @Autowired
    private SysPermModuleMapper sysPermModuleMapper;
    @Autowired
    private SysPermService sysPermService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysPermModule> mapper() {
        return sysPermModuleMapper;
    }

    /**
     * 保存新增的权限资源模块对象。
     *
     * @param sysPermModule 新增的权限资源模块对象。
     * @return 新增后的权限资源模块对象。
     */
    @Transactional(rollbackFor = Exception.class)
    public SysPermModule saveNew(SysPermModule sysPermModule) {
        sysPermModule.setModuleId(idGenerator.nextLongId());
        sysPermModule.setCreateTime(new Date());
        sysPermModule.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        sysPermModuleMapper.insert(sysPermModule);
        return sysPermModule;
    }

    /**
     * 更新权限资源模块对象。
     *
     * @param sysPermModule         更新的权限资源模块对象。
     * @param originalSysPermModule 原有的权限资源模块对象。
     * @return 更新成功返回true，否则false
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysPermModule sysPermModule, SysPermModule originalSysPermModule) {
        sysPermModule.setCreateTime(originalSysPermModule.getCreateTime());
        sysPermModule.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        return sysPermModuleMapper.updateByPrimaryKey(sysPermModule) != 0;
    }

    /**
     * 删除指定的权限资源模块。
     *
     * @param moduleId 权限资源模块主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long moduleId) {
        SysPermModule permModule = new SysPermModule();
        permModule.setModuleId(moduleId);
        permModule.setDeletedFlag(GlobalDeletedFlag.DELETED);
        return sysPermModuleMapper.updateByPrimaryKeySelective(permModule) != 0;
    }

    /**
     * 获取权限模块资源及其关联的权限资源列表。
     *
     * @return 权限资源模块及其关联的权限资源列表。
     */
    public List<SysPermModule> getPermModuleAndPermList() {
        return sysPermModuleMapper.getPermModuleAndPermList();
    }

    /**
     * 判断是否存在下级权限资源模块。
     *
     * @param moduleId 权限资源模块主键Id。
     * @return 存在返回true，否则false。
     */
    public boolean hasChildren(Long moduleId) {
        SysPermModule permModule = new SysPermModule();
        permModule.setParentId(moduleId);
        return this.getCountByFilter(permModule) > 0;
    }

    /**
     * 判断是否存在权限数据。
     *
     * @param moduleId 权限资源模块主键Id。
     * @return 存在返回true，否则false。
     */
    public boolean hasModulePerms(Long moduleId) {
        SysPerm filter = new SysPerm();
        filter.setModuleId(moduleId);
        return sysPermService.getCountByFilter(filter) > 0;
    }
}
