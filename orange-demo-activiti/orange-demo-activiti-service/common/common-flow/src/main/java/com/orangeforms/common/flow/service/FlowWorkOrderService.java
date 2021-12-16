package com.orangeforms.common.flow.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.flow.model.FlowWorkOrder;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.*;

/**
 * 工作流工单表数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowWorkOrderService extends IBaseService<FlowWorkOrder, Long> {

    /**
     * 保存新增对象。
     *
     * @param instance      流程实例对象。
     * @param dataId        流程实例的BusinessKey。
     * @param onlineTableId 在线数据表的主键Id。
     * @param tableName     面向静态表单所使用的表名。
     * @return 新增的工作流工单对象。
     */
    FlowWorkOrder saveNew(ProcessInstance instance, Object dataId, Long onlineTableId, String tableName);

    /**
     * 删除指定数据。
     *
     * @param workOrderId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long workOrderId);

    /**
     * 删除指定流程实例Id的关联工单。
     *
     * @param processInstanceId 流程实例Id。
     */
    void removeByProcessInstanceId(String processInstanceId);

    /**
     * 获取工作流工单单表查询结果。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowWorkOrder> getFlowWorkOrderList(FlowWorkOrder filter, String orderBy);

    /**
     * 获取工作流工单列表及其关联字典数据。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowWorkOrder> getFlowWorkOrderListWithRelation(FlowWorkOrder filter, String orderBy);

    /**
     * 根据流程实例Id，查询关联的工单对象。
     *
     * @param processInstanceId 流程实例Id。
     * @return 工作流工单对象。
     */
    FlowWorkOrder getFlowWorkOrderByProcessInstanceId(String processInstanceId);

    /**
     * 根据业务主键，查询是否存在指定的工单。
     *
     * @param tableName   静态表单工作流使用的数据表。
     * @param businessKey 业务数据主键Id。
     * @param unfinished  是否为没有结束工单。
     * @return 存在返回true，否则false。
     */
    boolean existByBusinessKey(String tableName, Object businessKey, boolean unfinished);

    /**
     * 根据流程实例Id，更新流程状态。
     *
     * @param processInstanceId 流程实例Id。
     * @param flowStatus        新的流程状态值。
     */
    void updateFlowStatusByProcessInstanceId(String processInstanceId, int flowStatus);
}
