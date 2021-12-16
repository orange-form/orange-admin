package com.orangeforms.common.flow.service;

import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.flow.model.FlowMessage;
import com.orangeforms.common.flow.model.FlowWorkOrder;

import java.util.List;

/**
 * 工作流消息数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowMessageService extends IBaseService<FlowMessage, Long> {

    /**
     * 保存新增对象。
     *
     * @param flowMessage 新增对象。
     * @return 保存后的消息对象。
     */
    FlowMessage saveNew(FlowMessage flowMessage);

    /**
     * 根据工单参数，保存催单消息对象。如果当前工单存在多个待办任务，则插入多条催办消息数据。
     *
     * @param flowWorkOrder 待催办的工单。
     */
    void saveNewRemindMessage(FlowWorkOrder flowWorkOrder);

    /**
     * 更新指定运行时任务Id的消费为已完成状态。
     *
     * @param taskId 运行时任务Id。
     */
    void updateFinishedStatusByTaskId(String taskId);

    /**
     * 更新指定流程实例Id的消费为已完成状态。
     *
     * @param processInstanceId 流程实例IdId。
     */
    void updateFinishedStatusByProcessInstanceId(String processInstanceId);

    /**
     * 获取当前用户的催办消息列表。
     *
     * @return 查询后的催办消息列表。
     */
    List<FlowMessage> getRemindingMessageListByUser();
}
