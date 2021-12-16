package com.orangeforms.common.flow.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.object.ResponseResult;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.flow.constant.FlowApprovalType;
import com.orangeforms.common.flow.constant.FlowConstant;
import com.orangeforms.common.flow.constant.FlowTaskStatus;
import com.orangeforms.common.flow.dto.FlowTaskCommentDto;
import com.orangeforms.common.flow.dto.FlowWorkOrderDto;
import com.orangeforms.common.flow.model.FlowEntry;
import com.orangeforms.common.flow.model.FlowEntryPublish;
import com.orangeforms.common.flow.model.FlowWorkOrder;
import com.orangeforms.common.flow.model.constant.FlowEntryStatus;
import com.orangeforms.common.flow.service.FlowApiService;
import com.orangeforms.common.flow.service.FlowEntryService;
import com.orangeforms.common.flow.vo.FlowWorkOrderVo;
import com.orangeforms.common.flow.vo.TaskInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 工作流操作的通用帮助对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Component
public class FlowOperationHelper {

    @Autowired
    private FlowEntryService flowEntryService;
    @Autowired
    private FlowApiService flowApiService;

    /**
     * 验证并获取流程对象。
     *
     * @param processDefinitionKey 流程引擎的流程定义标识。
     * @return 流程对象。
     */
    public ResponseResult<FlowEntry> verifyAndGetFlowEntry(String processDefinitionKey) {
        String errorMessage;
        FlowEntry flowEntry = flowEntryService.getFlowEntryByProcessDefinitionKey(processDefinitionKey);
        if (flowEntry == null) {
            errorMessage = "数据验证失败，该流程并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!flowEntry.getStatus().equals(FlowEntryStatus.PUBLISHED)) {
            errorMessage = "数据验证失败，该流程尚未发布，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        FlowEntryPublish flowEntryPublish =
                flowEntryService.getFlowEntryPublishById(flowEntry.getMainEntryPublishId());
        flowEntry.setMainFlowEntryPublish(flowEntryPublish);
        return ResponseResult.success(flowEntry);
    }

    /**
     * 工作流静态表单的参数验证工具方法。根据流程定义标识，获取关联的流程并对其进行合法性验证。
     *
     * @param processDefinitionKey 流程定义标识。
     * @return 返回流程对象。
     */
    public ResponseResult<FlowEntry> verifyFullAndGetFlowEntry(String processDefinitionKey) {
        String errorMessage;
        // 验证流程管理数据状态的合法性。
        ResponseResult<FlowEntry> flowEntryResult = this.verifyAndGetFlowEntry(processDefinitionKey);
        if (!flowEntryResult.isSuccess()) {
            return ResponseResult.errorFrom(flowEntryResult);
        }
        // 验证流程一个用户任务的合法性。
        FlowEntryPublish flowEntryPublish = flowEntryResult.getData().getMainFlowEntryPublish();
        if (!flowEntryPublish.getActiveStatus()) {
            errorMessage = "数据验证失败，当前流程发布对象已被挂起，不能启动新流程！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ResponseResult<TaskInfoVo> taskInfoResult =
                this.verifyAndGetInitialTaskInfo(flowEntryPublish, true);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        return flowEntryResult;
    }

    /**
     * 工作流静态表单的参数验证工具方法。根据参数验证并获取指定的流程任务对象。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @param flowTaskComment   流程审批对象。
     * @return 验证后的流程任务对象。
     */
    public ResponseResult<Task> verifySubmitAndGetTask(
            String processInstanceId, String taskId, FlowTaskCommentDto flowTaskComment) {
        // 验证流程任务的合法性。
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        ResponseResult<TaskInfoVo> taskInfoResult = this.verifyAndGetRuntimeTaskInfo(task);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        CallResult assigneeVerifyResult = flowApiService.verifyAssigneeOrCandidateAndClaim(task);
        if (!assigneeVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(assigneeVerifyResult);
        }
        if (StrUtil.isBlank(task.getBusinessKey())) {
            return ResponseResult.success(task);
        }
        String errorMessage;
        if (flowTaskComment != null && StrUtil.equals(flowTaskComment.getApprovalType(), FlowApprovalType.TRANSFER)) {
            if (StrUtil.isBlank(flowTaskComment.getDelegateAssginee())) {
                errorMessage = "数据验证失败，加签或转办任务指派人不能为空！！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success(task);
    }

    /**
     * 工作流静态表单的参数验证工具方法。根据参数验证并获取指定的历史流程实例对象。
     * 仅当登录用户为任务的分配人时，才能通过验证。
     *
     * @param processInstanceId 历史流程实例Id。
     * @param taskId            历史流程任务Id。
     * @return 验证后并返回的历史流程实例对象。
     */
    public ResponseResult<HistoricProcessInstance> verifyAndHistoricProcessInstance(String processInstanceId, String taskId) {
        String errorMessage;
        // 验证流程实例的合法性。
        HistoricProcessInstance instance = flowApiService.getHistoricProcessInstance(processInstanceId);
        if (instance == null) {
            errorMessage = "数据验证失败，指定的流程实例Id并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        String loginName = TokenData.takeFromRequest().getLoginName();
        if (StrUtil.isBlank(taskId)) {
            if (!StrUtil.equals(loginName, instance.getStartUserId())) {
                errorMessage = "数据验证失败，指定历史流程的发起人与当前用户不匹配！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        } else {
            HistoricTaskInstance taskInstance = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
            if (taskInstance == null) {
                errorMessage = "数据验证失败，指定的任务Id并不存在，请刷新后重试！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (!StrUtil.equals(loginName, taskInstance.getAssignee())) {
                errorMessage = "数据验证失败，历史任务的指派人与当前用户不匹配！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success(instance);
    }

    /**
     * 验证并获取流程的实时任务信息。
     *
     * @param task 流程引擎的任务对象。
     * @return 任务信息对象。
     */
    public ResponseResult<TaskInfoVo> verifyAndGetRuntimeTaskInfo(Task task) {
        String errorMessage;
        if (task == null) {
            errorMessage = "数据验证失败，指定的任务Id，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!flowApiService.isAssigneeOrCandidate(task)) {
            errorMessage = "数据验证失败，当前用户不是指派人也不是候选人之一！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (StrUtil.isBlank(task.getFormKey())) {
            errorMessage = "数据验证失败，指定任务的formKey属性不存在，请重新修改流程图！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TaskInfoVo taskInfo = JSON.parseObject(task.getFormKey(), TaskInfoVo.class);
        taskInfo.setTaskKey(task.getTaskDefinitionKey());
        return ResponseResult.success(taskInfo);
    }

    /**
     * 验证并获取启动任务的对象信息。
     *
     * @param flowEntryPublish 流程发布对象。
     * @param checkStarter     是否检查发起用户。
     * @return 第一个可执行的任务信息。
     */
    public ResponseResult<TaskInfoVo> verifyAndGetInitialTaskInfo(
            FlowEntryPublish flowEntryPublish, boolean checkStarter) {
        String errorMessage;
        if (StrUtil.isBlank(flowEntryPublish.getInitTaskInfo())) {
            errorMessage = "数据验证失败，当前流程发布的数据中，没有包含初始任务信息！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TaskInfoVo taskInfo = JSON.parseObject(flowEntryPublish.getInitTaskInfo(), TaskInfoVo.class);
        if (checkStarter) {
            String loginName = TokenData.takeFromRequest().getLoginName();
            if (!StrUtil.equalsAny(taskInfo.getAssignee(), loginName, FlowConstant.START_USER_NAME_VAR)) {
                errorMessage = "数据验证失败，该工作流第一个用户任务的指派人并非当前用户，不能执行该操作！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success(taskInfo);
    }

    /**
     * 判断当前用户是否有当前流程实例的数据上传或下载权限。
     * 如果taskId为空，则验证当前用户是否为当前流程实例的发起人，否则判断是否为当前任务的指派人或候选人。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @return 验证结果。
     */
    public ResponseResult<Void> verifyUploadOrDownloadPermission(String processInstanceId, String taskId) {
        String errorMessage;
        if (StrUtil.isBlank(taskId)) {
            if (!flowApiService.isProcessInstanceStarter(processInstanceId)) {
                errorMessage = "数据验证失败，当前用户并非指派人或候选人，因此没有权限下载！";
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
            }
        } else {
            TaskInfo task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
            if (task == null) {
                task = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
                if (task == null) {
                    errorMessage = "数据验证失败，指定任务Id不存在！";
                    return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
                }
            }
            if (!flowApiService.isAssigneeOrCandidate(task)) {
                errorMessage = "数据验证失败，当前用户并非指派人或候选人，因此没有权限下载！";
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
            }
        }
        return ResponseResult.success();
    }

    /**
     * 根据已有的过滤对象，补充添加缺省过滤条件。如流程标识、创建用户等。
     *
     * @param filterDto            工单过滤对象。
     * @param processDefinitionKey 流程标识。
     * @return 创建并转换后的流程工单过滤对象。
     */
    public FlowWorkOrder makeWorkOrderFilter(FlowWorkOrderDto filterDto, String processDefinitionKey) {
        FlowWorkOrder filter = MyModelUtil.copyTo(filterDto, FlowWorkOrder.class);
        if (filter == null) {
            filter = new FlowWorkOrder();
        }
        filter.setProcessDefinitionKey(processDefinitionKey);
        filter.setCreateUserId(TokenData.takeFromRequest().getUserId());
        return filter;
    }

    /**
     * 组装工作流工单列表中的流程任务数据。
     *
     * @param flowWorkOrderVoList 工作流工单列表。
     */
    public void buildWorkOrderTaskInfo(List<FlowWorkOrderVo> flowWorkOrderVoList) {
        if (CollUtil.isEmpty(flowWorkOrderVoList)) {
            return;
        }
        Set<String> definitionIdSet =
                flowWorkOrderVoList.stream().map(FlowWorkOrderVo::getProcessDefinitionId).collect(Collectors.toSet());
        List<FlowEntryPublish> flowEntryPublishList = flowEntryService.getFlowEntryPublishList(definitionIdSet);
        Map<String, FlowEntryPublish> flowEntryPublishMap =
                flowEntryPublishList.stream().collect(Collectors.toMap(FlowEntryPublish::getProcessDefinitionId, c -> c));
        for (FlowWorkOrderVo flowWorkOrderVo : flowWorkOrderVoList) {
            FlowEntryPublish flowEntryPublish = flowEntryPublishMap.get(flowWorkOrderVo.getProcessDefinitionId());
            flowWorkOrderVo.setInitTaskInfo(flowEntryPublish.getInitTaskInfo());
        }
        List<String> unfinishedProcessInstanceIds = flowWorkOrderVoList.stream()
                .filter(c -> !c.getFlowStatus().equals(FlowTaskStatus.FINISHED))
                .map(FlowWorkOrderVo::getProcessInstanceId)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(unfinishedProcessInstanceIds)) {
            return;
        }
        List<Task> taskList = flowApiService.getTaskListByProcessInstanceIds(unfinishedProcessInstanceIds);
        Map<String, List<Task>> taskMap =
                taskList.stream().collect(Collectors.groupingBy(Task::getProcessInstanceId));
        for (FlowWorkOrderVo flowWorkOrderVo : flowWorkOrderVoList) {
            List<Task> instanceTaskList = taskMap.get(flowWorkOrderVo.getProcessInstanceId());
            if (instanceTaskList == null) {
                continue;
            }
            JSONArray taskArray = new JSONArray();
            for (Task task : instanceTaskList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("taskId", task.getId());
                jsonObject.put("taskName", task.getName());
                jsonObject.put("taskKey", task.getTaskDefinitionKey());
                jsonObject.put("assignee", task.getAssignee());
                taskArray.add(jsonObject);
            }
            flowWorkOrderVo.setRuntimeTaskInfoList(taskArray);
        }
    }

    /**
     * 组装工作流工单中的业务数据。
     *
     * @param workOrderVoList 工单列表。
     * @param dataList        业务数据列表。
     * @param idGetter        获取业务对象主键字段的返回方法。
     * @param <T>             业务主对象类型。
     * @param <K>             业务主对象的主键字段类型。
     */
    public <T, K> void buildWorkOrderBusinessData(
            List<FlowWorkOrderVo> workOrderVoList, List<T> dataList, Function<T, K> idGetter) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        Map<Object, T> dataMap = dataList.stream().collect(Collectors.toMap(idGetter, c -> c));
        K id = idGetter.apply(dataList.get(0));
        for (FlowWorkOrderVo flowWorkOrderVo : workOrderVoList) {
            Object dataId = flowWorkOrderVo.getBusinessKey();
            if (id instanceof Long) {
                dataId = Long.valueOf(flowWorkOrderVo.getBusinessKey());
            } else if (id instanceof Integer) {
                dataId = Integer.valueOf(flowWorkOrderVo.getBusinessKey());
            }
            T data = dataMap.get(dataId);
            if (data != null) {
                flowWorkOrderVo.setMasterData(BeanUtil.beanToMap(data));
            }
        }
    }
}
