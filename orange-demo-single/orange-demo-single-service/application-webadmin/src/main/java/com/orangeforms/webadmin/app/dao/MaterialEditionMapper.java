package com.orangeforms.webadmin.app.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.webadmin.app.model.MaterialEdition;

import java.util.*;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface MaterialEditionMapper extends BaseDaoMapper<MaterialEdition> {

    /**
     * 批量插入对象列表。
     *
     * @param materialEditionList 新增对象列表。
     */
    void insertList(List<MaterialEdition> materialEditionList);
}
