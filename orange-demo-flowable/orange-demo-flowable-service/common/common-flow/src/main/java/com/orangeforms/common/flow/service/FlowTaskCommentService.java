package com.orangeforms.common.flow.service;

import com.orangeforms.common.flow.model.*;
import com.orangeforms.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 流程任务批注数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowTaskCommentService extends IBaseService<FlowTaskComment, Long> {

    /**
     * 保存新增对象。
     *
     * @param flowTaskComment 新增对象。
     * @return 返回新增对象。
     */
    FlowTaskComment saveNew(FlowTaskComment flowTaskComment);

    /**
     * 查询指定流程实例Id下的所有审批任务的批注。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果集。
     */
    List<FlowTaskComment> getFlowTaskCommentList(String processInstanceId);

    /**
     * 查询与指定流程任务Id集合关联的所有审批任务的批注。
     *
     * @param taskIdSet 流程任务Id集合。
     * @return 查询结果集。
     */
    List<FlowTaskComment> getFlowTaskCommentListByTaskIds(Set<String> taskIdSet);

    /**
     * 获取指定流程实例的最后一条审批任务。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果。
     */
    FlowTaskComment getLatestFlowTaskComment(String processInstanceId);
}
