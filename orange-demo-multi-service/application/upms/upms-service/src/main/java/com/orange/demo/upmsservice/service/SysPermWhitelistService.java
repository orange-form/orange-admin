package com.orange.demo.upmsservice.service;

import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upmsservice.dao.SysPermWhitelistMapper;
import com.orange.demo.upmsservice.model.SysPermWhitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 白名单数据服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service
public class SysPermWhitelistService extends BaseService<SysPermWhitelist, SysPermWhitelist, String> {

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
