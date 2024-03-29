package com.orangeforms.upmsservice.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.upmsservice.model.SysPermWhitelist;

import java.util.List;

/**
 * 白名单数据服务接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysPermWhitelistService extends IBaseService<SysPermWhitelist, String> {

    /**
     * 获取白名单权限资源的列表。
     *
     * @return 白名单权限资源地址列表。
     */
    List<String> getWhitelistPermList();
}
