package com.orange.admin.upms.service;

import com.orange.admin.common.biz.base.service.BaseBizService;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.upms.dao.SysPermWhitelistMapper;
import com.orange.admin.upms.model.SysPermWhitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限资源白名单数据服务类。
 * 白名单中的权限资源，可以不受权限控制，任何用户皆可访问，一般用于常用的字典数据列表接口。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Service
public class SysPermWhitelistService extends BaseBizService<SysPermWhitelist, String> {

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
