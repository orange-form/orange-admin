package com.orange.demo.upms.service;

import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upms.dao.SysPermWhitelistMapper;
import com.orange.demo.upms.model.SysPermWhitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限资源白名单数据服务类。
 * 白名单中的权限资源，可以不受权限控制，任何用户皆可访问，一般用于常用的字典数据列表接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Service
public class SysPermWhitelistService extends BaseService<SysPermWhitelist, String> {

    @Autowired
    private SysPermWhitelistMapper sysPermWhitelistMapper;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysPermWhitelist> mapper() {
        return sysPermWhitelistMapper;
    }
}
