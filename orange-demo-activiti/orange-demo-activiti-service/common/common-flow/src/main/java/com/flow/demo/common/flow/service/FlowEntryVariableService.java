package com.flow.demo.common.flow.service;

import com.flow.demo.common.flow.model.*;
import com.flow.demo.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 流程变量数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowEntryVariableService extends IBaseService<FlowEntryVariable, Long> {

    /**
     * 保存新增对象。
     *
     * @param flowEntryVariable 新增对象。
     * @return 返回新增对象。
     */
    FlowEntryVariable saveNew(FlowEntryVariable flowEntryVariable);

    /**
     * 更新数据对象。
     *
     * @param flowEntryVariable         更新的对象。
     * @param originalFlowEntryVariable 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(FlowEntryVariable flowEntryVariable, FlowEntryVariable originalFlowEntryVariable);

    /**
     * 删除指定数据。
     *
     * @param variableId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long variableId);

    /**
     * 删除指定流程Id的所有变量。
     *
     * @param entryId 流程Id。
     */
    void removeByEntryId(Long entryId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getFlowEntryVariableListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowEntryVariable> getFlowEntryVariableList(FlowEntryVariable filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getFlowEntryVariableList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowEntryVariable> getFlowEntryVariableListWithRelation(FlowEntryVariable filter, String orderBy);
}
