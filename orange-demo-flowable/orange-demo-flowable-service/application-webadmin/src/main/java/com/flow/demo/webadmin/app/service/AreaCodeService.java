package com.flow.demo.webadmin.app.service;

import com.flow.demo.common.core.base.service.IBaseDictService;
import com.flow.demo.webadmin.app.model.AreaCode;

import java.util.Collection;

/**
 * 行政区划的Service接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface AreaCodeService extends IBaseDictService<AreaCode, Long> {

    /**
     * 根据上级行政区划Id，获取其下级行政区划列表。
     *
     * @param parentId 上级行政区划Id。
     * @return 下级行政区划列表。
     */
    Collection<AreaCode> getListByParentId(Long parentId);
}
