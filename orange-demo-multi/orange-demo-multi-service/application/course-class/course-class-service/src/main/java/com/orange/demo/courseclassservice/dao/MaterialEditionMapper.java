package com.orange.demo.courseclassservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.courseclassservice.model.MaterialEdition;

import java.util.*;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface MaterialEditionMapper extends BaseDaoMapper<MaterialEdition> {

    /**
     * 批量插入对象列表。
     *
     * @param materialEditionList 新增对象列表。
     */
    void insertList(List<MaterialEdition> materialEditionList);
}
