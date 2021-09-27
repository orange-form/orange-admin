package com.flow.demo.webadmin.app.service.impl;

import com.flow.demo.webadmin.app.service.AreaCodeService;
import com.flow.demo.webadmin.app.dao.AreaCodeMapper;
import com.flow.demo.webadmin.app.model.AreaCode;
import com.flow.demo.common.core.cache.MapTreeDictionaryCache;
import com.flow.demo.common.core.base.service.BaseDictService;
import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * 行政区划的Service类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Service("areaCodeService")
public class AreaCodeServiceImpl extends BaseDictService<AreaCode, Long> implements AreaCodeService {

    @Autowired
    private AreaCodeMapper areaCodeMapper;

    public AreaCodeServiceImpl() {
        super();
        this.dictionaryCache = MapTreeDictionaryCache.create(AreaCode::getAreaId, AreaCode::getParentId);
    }

    @PostConstruct
    public void init() {
        this.reloadCachedData(true);
    }

    @Override
    protected BaseDaoMapper<AreaCode> mapper() {
        return areaCodeMapper;
    }

    /**
     * 根据上级行政区划Id，获取其下级行政区划列表。
     *
     * @param parentId 上级行政区划Id。
     * @return 下级行政区划列表。
     */
    @Override
    public Collection<AreaCode> getListByParentId(Long parentId) {
        return ((MapTreeDictionaryCache<Long, AreaCode>) dictionaryCache).getListByParentId(parentId);
    }
}
