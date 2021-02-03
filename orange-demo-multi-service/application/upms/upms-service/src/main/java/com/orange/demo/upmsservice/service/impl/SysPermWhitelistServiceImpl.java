package com.orange.demo.upmsservice.service.impl;

import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.upmsservice.dao.SysPermWhitelistMapper;
import com.orange.demo.upmsservice.model.SysPermWhitelist;
import com.orange.demo.upmsservice.service.SysPermWhitelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 白名单数据服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
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
