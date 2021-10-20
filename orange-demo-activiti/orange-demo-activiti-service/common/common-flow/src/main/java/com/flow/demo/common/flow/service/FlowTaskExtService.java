package com.flow.demo.common.flow.service;

import com.flow.demo.common.flow.model.*;
import com.flow.demo.common.core.base.service.IBaseService;

import java.util.List;

/**
 * 流程任务扩展数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowTaskExtService extends IBaseService<FlowTaskExt, String> {

    /**
     * 批量插入流程任务扩展信息列表。
     *
     * @param flowTaskExtList 流程任务扩展信息列表。
     */
    void saveBatch(List<FlowTaskExt> flowTaskExtList);

    /**
     * 查询指定的流程任务扩展对象。
     * @param processDefinitionId 流程引擎的定义Id。
     * @param taskId              流程引擎的任务Id。
     * @return 查询结果。
     */
    FlowTaskExt getByProcessDefinitionIdAndTaskId(String processDefinitionId, String taskId);

    /**
     * 查询指定的流程定义的任务扩展对象。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @return 查询结果。
     */
    List<FlowTaskExt> getByProcessDefinitionId(String processDefinitionId);
}
