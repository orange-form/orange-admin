package com.orangeforms.common.flow.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.flow.model.*;

import java.util.List;

/**
 * FlowCategory数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowCategoryService extends IBaseService<FlowCategory, Long> {

    /**
     * 保存新增对象。
     *
     * @param flowCategory 新增对象。
     * @return 返回新增对象。
     */
    FlowCategory saveNew(FlowCategory flowCategory);

    /**
     * 更新数据对象。
     *
     * @param flowCategory         更新的对象。
     * @param originalFlowCategory 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(FlowCategory flowCategory, FlowCategory originalFlowCategory);

    /**
     * 删除指定数据。
     *
     * @param categoryId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long categoryId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getFlowCategoryListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowCategory> getFlowCategoryList(FlowCategory filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getFlowCategoryList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowCategory> getFlowCategoryListWithRelation(FlowCategory filter, String orderBy);
}
