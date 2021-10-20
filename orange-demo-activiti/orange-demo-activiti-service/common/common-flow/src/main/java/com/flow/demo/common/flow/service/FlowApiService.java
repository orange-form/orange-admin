package com.flow.demo.common.flow.service;

import com.alibaba.fastjson.JSONObject;
import com.flow.demo.common.core.object.CallResult;
import com.flow.demo.common.core.object.MyPageData;
import com.flow.demo.common.core.object.MyPageParam;
import com.flow.demo.common.flow.model.FlowTaskComment;
import com.flow.demo.common.flow.vo.FlowTaskVo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程引擎API的接口封装服务。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowApiService {

    /**
     * 启动流程实例。
     *
     * @param processDefinitionId 流程定义Id。
     */
    void start(String processDefinitionId);

    /**
     * 启动流程实例，如果当前登录用户为第一个用户任务的指派者，或者Assginee为流程启动人变量时，
     * 则自动完成第一个用户任务。
     *
     * @param processDefinitionId 流程定义Id。
     * @param dataId              当前流程主表的主键数据。
     * @param flowTaskComment     审批对象。
     * @param taskVariableData    流程任务的变量数据。
     * @return 新启动的流程实例。
     */
    ProcessInstance startAndTakeFirst(
            String processDefinitionId, Object dataId, FlowTaskComment flowTaskComment, JSONObject taskVariableData);

    /**
     * 多实例加签。
     *
     * @param startTaskInstance       会签对象的发起任务实例。
     * @param multiInstanceActiveTask 正在执行的多实例任务对象。
     * @param newAssignees            新指派人，多个指派人之间逗号分隔。
     */
    void submitConsign(HistoricTaskInstance startTaskInstance, Task multiInstanceActiveTask, String newAssignees);

    /**
     * 完成任务，同时提交审批数据。
     *
     * @param task             工作流任务对象。
     * @param flowTaskComment  审批对象。
     * @param taskVariableData 流程任务的变量数据。
     */
    void completeTask(Task task, FlowTaskComment flowTaskComment, JSONObject taskVariableData);

    /**
     * 判断当前登录用户是否为流程实例中的用户任务的指派人。或是候选人之一，如果是候选人则拾取该任务并成为指派人。
     * 如果都不是，就会返回具体的错误信息。
     *
     * @param task 流程实例中的用户任务。
     * @return 调用结果。
     */
    CallResult verifyAssigneeOrCandidateAndClaim(Task task);

    /**
     * 初始化并返回流程实例的变量Map。
     * @param processDefinitionId 流程定义Id。
     * @return 初始化后的流程实例变量Map。
     */
    Map<String, Object> initAndGetProcessInstanceVariables(String processDefinitionId);

    /**
     * 判断当前登录用户是否为流程实例中的用户任务的指派人。或是候选人之一。
     *
     * @param task 流程实例中的用户任务。
     * @return 是返回true，否则false。
     */
    boolean isAssigneeOrCandidate(TaskInfo task);

    /**
     * 判断当前登录用户是否为流程实例的发起人。
     *
     * @param processInstanceId 流程实例Id。
     * @return 是返回true，否则false。
     */
    boolean isProcessInstanceStarter(String processInstanceId);

    /**
     * 为流程实例设置BusinessKey。
     *
     * @param processInstanceId 流程实例Id。
     * @param dataId            通常为主表的主键Id。
     */
    void setBusinessKeyForProcessInstance(String processInstanceId, Object dataId);

    /**
     * 判断指定的流程实例Id是否存在。
     *
     * @param processInstanceId 流程实例Id。
     * @return 存在返回true，否则false。
     */
    boolean existActiveProcessInstance(String processInstanceId);

    /**
     * 获取指定的流程实例对象。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例对象。
     */
    ProcessInstance getProcessInstance(String processInstanceId);

    /**
     * 获取流程实例的列表。
     *
     * @param processInstanceIdSet 流程实例Id集合。
     * @return 流程实例列表。
     */
    List<ProcessInstance> getProcessInstanceList(Set<String> processInstanceIdSet);

    /**
     * 根据流程部署Id查询流程定义对象。
     *
     * @param deployId 流程部署Id。
     * @return 流程定义对象。
     */
    ProcessDefinition getProcessDefinitionByDeployId(String deployId);

    /**
     * 获取流程定义的列表。
     *
     * @param processDefinitionIdSet 流程定义Id集合。
     * @return 流程定义列表。
     */
    List<ProcessDefinition> getProcessDefinitionList(Set<String> processDefinitionIdSet);

    /**
     * 挂起流程定义对象。
     *
     * @param processDefinitionId 流程定义Id。
     */
    void suspendProcessDefinition(String processDefinitionId);

    /**
     * 激活流程定义对象。
     *
     * @param processDefinitionId 流程定义Id。
     */
    void activateProcessDefinition(String processDefinitionId);

    /**
     * 获取指定流程定义的BpmnModel。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 关联的BpmnModel。
     */
    BpmnModel getBpmnModelByDefinitionId(String processDefinitionId);

    /**
     * 获取流程实例的变量。
     *
     * @param processInstanceId 流程实例Id。
     * @param variableName      变量名。
     * @return 变量值。
     */
    Object getProcessInstanceVariable(String processInstanceId, String variableName);

    /**
     * 获取指定流程实例和任务Id的当前活动任务。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @return 当前流程实例的活动任务。
     */
    Task getProcessInstanceActiveTask(String processInstanceId, String taskId);

    /**
     * 获取指定流程实例的当前活动任务列表。
     *
     * @param processInstanceId 流程实例Id。
     * @return 当前流程实例的活动任务。
     */
    List<Task> getProcessInstanceActiveTaskList(String processInstanceId);

    /**
     * 获取用户的任务列表。这其中包括当前用户作为指派人和候选人。
     *
     * @param username      指派人。
     * @param definitionKey 流程定义的标识。
     * @param pageParam     分页对象。
     * @return 用户的任务列表。
     */
    MyPageData<Task> getTaskListByUserName(String username, String definitionKey, MyPageParam pageParam);

    /**
     * 获取用户的任务数量。这其中包括当前用户作为指派人和候选人。
     *
     * @param username      指派人。
     * @return 用户的任务数量。
     */
    long getTaskCountByUserName(String username);

    /**
     * 获取流程实例Id集合的运行时任务列表。
     *
     * @param processInstanceIdSet 流程实例Id集合。
     * @return 运行时任务列表。
     */
    List<Task> getTaskListByProcessInstanceIds(List<String> processInstanceIdSet);

    /**
     * 将流程任务列表数据，转换为前端可以显示的流程对象。
     *
     * @param taskList 流程引擎中的任务列表。
     * @return 前端可以显示的流程任务列表。
     */
    List<FlowTaskVo> convertToFlowTaskList(List<Task> taskList);

    /**
     * 添加流程实例结束的监听器。
     *
     * @param bpmnModel     流程模型。
     * @param listenerClazz 流程监听器的Class对象。
     */
    void addProcessInstanceEndListener(BpmnModel bpmnModel, Class<? extends ExecutionListener> listenerClazz);

    /**
     * 添加流程任务创建的任务监听器。
     *
     * @param userTask      用户任务。
     * @param listenerClazz 任务监听器。
     */
    void addTaskCreateListener(UserTask userTask, Class<? extends TaskListener> listenerClazz);

    /**
     * 获取流程实例的历史流程实例。
     *
     * @param processInstanceId 流程实例Id。
     * @return 历史流程实例。
     */
    HistoricProcessInstance getHistoricProcessInstance(String processInstanceId);

    /**
     * 获取流程实例的历史流程实例列表。
     *
     * @param processInstanceIdSet 流程实例Id集合。
     * @return 历史流程实例列表。
     */
    List<HistoricProcessInstance> getHistoricProcessInstanceList(Set<String> processInstanceIdSet);

    /**
     * 查询历史流程实例的列表。
     *
     * @param processDefinitionKey  流程标识名。
     * @param processDefinitionName 流程名。
     * @param startUser             流程发起用户。
     * @param beginDate             流程发起开始时间。
     * @param endDate               流程发起结束时间。
     * @param pageParam             分页对象。
     * @param finishedOnly          仅仅返回已经结束的流程。
     * @return 分页后的查询列表对象。
     * @throws ParseException 日期参数解析失败。
     */
    MyPageData<HistoricProcessInstance> getHistoricProcessInstanceList(
            String processDefinitionKey,
            String processDefinitionName,
            String startUser,
            String beginDate,
            String endDate,
            MyPageParam pageParam,
            boolean finishedOnly) throws ParseException;

    /**
     * 获取流程实例的已完成历史任务列表。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例已完成的历史任务列表。
     */
    List<HistoricActivityInstance> getHistoricActivityInstanceList(String processInstanceId);

    /**
     * 获取当前用户的历史已办理任务列表。
     *
     * @param processDefinitionName 流程名。
     * @param beginDate             流程发起开始时间。
     * @param endDate               流程发起结束时间。
     * @param pageParam             分页对象。
     * @return 分页后的查询列表对象。
     * @throws ParseException 日期参数解析失败。
     */
    MyPageData<HistoricTaskInstance> getHistoricTaskInstanceFinishedList(
            String processDefinitionName,
            String beginDate,
            String endDate,
            MyPageParam pageParam) throws ParseException;

    /**
     * 获取指定的历史任务实例。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            任务Id。
     * @return 历史任务实例。
     */
    HistoricTaskInstance getHistoricTaskInstance(String processInstanceId, String taskId);

    /**
     * 获取流程实例的待完成任务列表。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程实例待完成的任务列表。
     */
    List<HistoricActivityInstance> getHistoricUnfinishedInstanceList(String processInstanceId);

    /**
     * 终止流程实例，将任务从当前节点直接流转到主流程的结束事件。
     *
     * @param processInstanceId 流程实例Id。
     * @param stopReason        停止原因。
     * @param forCancel         是否由取消工单触发。
     * @return 执行结果。
     */
    CallResult stopProcessInstance(String processInstanceId, String stopReason, boolean forCancel);

    /**
     * 删除流程实例。
     *
     * @param processInstanceId 流程实例Id。
     */
    void deleteProcessInstance(String processInstanceId);

    /**
     * 获取任务的指定本地变量。
     *
     * @param taskId       任务Id。
     * @param variableName 变量名。
     * @return 变量值。
     */
    Object getTaskVariable(String taskId, String variableName);
}
