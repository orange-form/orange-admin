package com.orangeforms.webadmin.upms.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.webadmin.upms.model.SysPermModule;

import java.util.List;

/**
 * 权限资源模块数据访问操作接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysPermModuleMapper extends BaseDaoMapper<SysPermModule> {

    /**
     * 获取整个权限模块和权限关联后的全部数据。
     *
     * @return 关联的权限模块和权限资源列表。
     */
    List<SysPermModule> getPermModuleAndPermList();
}
