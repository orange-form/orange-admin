package com.orange.admin.app.service;

import com.orange.admin.app.dao.AreaCodeMapper;
import com.orange.admin.app.model.AreaCode;
import com.orange.admin.common.core.cache.MapTreeDictionaryCache;
import com.orange.admin.common.core.base.service.BaseDictService;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;

/**
 * 行政区划的Service类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Service
public class AreaCodeService extends BaseDictService<AreaCode, Long> {

    @Autowired
    private AreaCodeMapper areaCodeMapper;

    public AreaCodeService() {
        super();
        this.dictionaryCache = MapTreeDictionaryCache.create(AreaCode::getAreaId, AreaCode::getParentId);
    }

    @Override
    protected BaseDaoMapper<AreaCode> mapper() {
        return areaCodeMapper;
    }

    /**
     * 加载数据库数据到内存缓存。
     */
    @Override
    public void loadCachedData() {
        Example e = new Example(AreaCode.class);
        e.orderBy("areaLevel");
        List<AreaCode> areaCodeList = areaCodeMapper.selectByExample(e);
        dictionaryCache.putAll(areaCodeList);
    }

    /**
     * 根据上级行政区划Id，获取其下级行政区划列表。
     *
     * @param parentId 上级行政区划Id。
     * @return 下级行政区划列表。
     */
    public Collection<AreaCode> getListByParentId(Long parentId) {
        return ((MapTreeDictionaryCache<Long, AreaCode>) dictionaryCache).getListByParentId(parentId);
    }
}
