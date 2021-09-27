package com.flow.demo.webadmin.upms.service;

import com.flow.demo.common.core.base.service.IBaseService;
import com.flow.demo.webadmin.upms.model.SysPermWhitelist;

import java.util.List;

/**
 * 权限资源白名单数据服务接口。
 * 白名单中的权限资源，可以不受权限控制，任何用户皆可访问，一般用于常用的字典数据列表接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface SysPermWhitelistService extends IBaseService<SysPermWhitelist, String> {

    /**
     * 获取白名单权限资源的列表。
     *
     * @return 白名单权限资源地址列表。
     */
    List<String> getWhitelistPermList();
}
