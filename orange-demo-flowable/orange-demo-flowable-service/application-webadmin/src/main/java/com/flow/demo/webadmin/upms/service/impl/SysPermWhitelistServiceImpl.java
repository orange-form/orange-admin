package com.flow.demo.webadmin.upms.service.impl;

import com.flow.demo.common.core.base.service.BaseService;
import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.webadmin.upms.dao.SysPermWhitelistMapper;
import com.flow.demo.webadmin.upms.model.SysPermWhitelist;
import com.flow.demo.webadmin.upms.service.SysPermWhitelistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限资源白名单数据服务类。
 * 白名单中的权限资源，可以不受权限控制，任何用户皆可访问，一般用于常用的字典数据列表接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("sysPermWhitelistService")
public class SysPermWhitelistServiceImpl extends BaseService<SysPermWhitelist, String> implements SysPermWhitelistService {

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

    /**
     * 获取白名单权限资源的列表。
     *
     * @return 白名单权限资源地址列表。
     */
    @Override
    public List<String> getWhitelistPermList() {
        List<SysPermWhitelist> dataList = this.getAllList();
        Function<SysPermWhitelist, String> getterFunc = SysPermWhitelist::getPermUrl;
        return dataList.stream()
                .filter(x -> getterFunc.apply(x) != null).map(getterFunc).collect(Collectors.toList());
    }
}
