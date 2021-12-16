package com.orangeforms.webadmin.upms.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.webadmin.upms.model.SysPermModule;

import java.util.*;

/**
 * 权限资源模块数据服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysPermModuleService extends IBaseService<SysPermModule, Long> {

    /**
     * 保存新增的权限资源模块对象。
     *
     * @param sysPermModule 新增的权限资源模块对象。
     * @return 新增后的权限资源模块对象。
     */
    SysPermModule saveNew(SysPermModule sysPermModule);

    /**
     * 更新权限资源模块对象。
     *
     * @param sysPermModule         更新的权限资源模块对象。
     * @param originalSysPermModule 原有的权限资源模块对象。
     * @return 更新成功返回true，否则false
     */
    boolean update(SysPermModule sysPermModule, SysPermModule originalSysPermModule);

    /**
     * 删除指定的权限资源模块。
     *
     * @param moduleId 权限资源模块主键Id。
     * @return 删除成功返回true，否则false。
     */
    boolean remove(Long moduleId);

    /**
     * 获取权限模块资源及其关联的权限资源列表。
     *
     * @return 权限资源模块及其关联的权限资源列表。
     */
    List<SysPermModule> getPermModuleAndPermList();

    /**
     * 判断是否存在下级权限资源模块。
     *
     * @param moduleId 权限资源模块主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasChildren(Long moduleId);

    /**
     * 判断是否存在权限数据。
     *
     * @param moduleId 权限资源模块主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasModulePerms(Long moduleId);
}
