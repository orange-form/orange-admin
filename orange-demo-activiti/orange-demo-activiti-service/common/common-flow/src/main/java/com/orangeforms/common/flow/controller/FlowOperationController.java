package com.orangeforms.common.flow.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.flow.constant.FlowConstant;
import com.orangeforms.common.flow.constant.FlowTaskStatus;
import com.orangeforms.common.flow.model.*;
import com.orangeforms.common.flow.service.*;
import com.orangeforms.common.flow.util.FlowOperationHelper;
import com.orangeforms.common.flow.vo.FlowTaskCommentVo;
import com.orangeforms.common.flow.vo.FlowTaskVo;
import com.orangeforms.common.flow.vo.TaskInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程操作接口类
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-flow.urlPrefix}/flowOperation")
public class FlowOperationController {

    @Autowired
    private FlowEntryService flowEntryService;
    @Autowired
    private FlowTaskCommentService flowTaskCommentService;
    @Autowired
    private FlowTaskExtService flowTaskExtService;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowWorkOrderService flowWorkOrderService;
    @Autowired
    private FlowMessageService flowMessageService;
    @Autowired
    private FlowOperationHelper flowOperationHelper;

    /**
     * 根据指定流程的主版本，发起一个流程实例。
     *
     * @param processDefinitionKey 流程标识。
     * @return 应答结果对象。
     */
    @PostMapping("/startOnly")
    public ResponseResult<Void> startOnly(@MyRequestBody(required = true) String processDefinitionKey) {
        // 1. 验证流程数据的合法性。
        ResponseResult<FlowEntry> flowEntryResult = flowOperationHelper.verifyAndGetFlowEntry(processDefinitionKey);
        if (!flowEntryResult.isSuccess()) {
            return ResponseResult.errorFrom(flowEntryResult);
        }
        // 2. 验证流程一个用户任务的合法性。
        FlowEntryPublish flowEntryPublish = flowEntryResult.getData().getMainFlowEntryPublish();
        ResponseResult<TaskInfoVo> taskInfoResult =
                flowOperationHelper.verifyAndGetInitialTaskInfo(flowEntryPublish, false);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        flowApiService.start(flowEntryPublish.getProcessDefinitionId(), null);
        return ResponseResult.success();
    }

    /**
     * 获取开始节点之后的第一个任务节点的数据。
     *
     * @param processDefinitionKey 流程标识。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewInitialTaskInfo")
    public ResponseResult<TaskInfoVo> viewInitialTaskInfo(@RequestParam String processDefinitionKey) {
        ResponseResult<FlowEntry> flowEntryResult = flowOperationHelper.verifyAndGetFlowEntry(processDefinitionKey);
        if (!flowEntryResult.isSuccess()) {
            return ResponseResult.errorFrom(flowEntryResult);
        }
        FlowEntryPublish flowEntryPublish = flowEntryResult.getData().getMainFlowEntryPublish();
        String initTaskInfo = flowEntryPublish.getInitTaskInfo();
        TaskInfoVo taskInfo = StrUtil.isBlank(initTaskInfo)
                ? null : JSON.parseObject(initTaskInfo, TaskInfoVo.class);
        if (taskInfo != null) {
            String loginName = TokenData.takeFromRequest().getLoginName();
            taskInfo.setAssignedMe(StrUtil.equalsAny(
                    taskInfo.getAssignee(), loginName, FlowConstant.START_USER_NAME_VAR));
        }
        return ResponseResult.success(taskInfo);
    }

    /**
     * 获取流程运行时指定任务的信息。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param processInstanceId   流程引擎的实例Id。
     * @param taskId              流程引擎的任务Id。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewRuntimeTaskInfo")
    public ResponseResult<TaskInfoVo> viewRuntimeTaskInfo(
            @RequestParam String processDefinitionId,
            @RequestParam String processInstanceId,
            @RequestParam String taskId) {
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        ResponseResult<TaskInfoVo> taskInfoResult = flowOperationHelper.verifyAndGetRuntimeTaskInfo(task);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        TaskInfoVo taskInfoVo = taskInfoResult.getData();
        FlowTaskExt flowTaskExt =
                flowTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskInfoVo.getTaskKey());
        if (flowTaskExt != null) {
            if (StrUtil.isNotBlank(flowTaskExt.getOperationListJson())) {
                taskInfoVo.setOperationList(JSON.parseArray(flowTaskExt.getOperationListJson(), JSONObject.class));
            }
            if (StrUtil.isNotBlank(flowTaskExt.getVariableListJson())) {
                taskInfoVo.setVariableList(JSON.parseArray(flowTaskExt.getVariableListJson(), JSONObject.class));
            }
        }
        return ResponseResult.success(taskInfoVo);
    }

    /**
     * 获取流程运行时指定任务的信息。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param processInstanceId   流程引擎的实例Id。
     * @param taskId              流程引擎的任务Id。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewHistoricTaskInfo")
    public ResponseResult<TaskInfoVo> viewHistoricTaskInfo(
            @RequestParam String processDefinitionId,
            @RequestParam String processInstanceId,
            @RequestParam String taskId) {
        String errorMessage;
        HistoricTaskInstance taskInstance = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
        String loginName = TokenData.takeFromRequest().getLoginName();
        if (!StrUtil.equals(taskInstance.getAssignee(), loginName)) {
            errorMessage = "数据验证失败，当前用户不是指派人！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TaskInfoVo taskInfoVo = JSON.parseObject(taskInstance.getFormKey(), TaskInfoVo.class);
        FlowTaskExt flowTaskExt =
                flowTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskInstance.getTaskDefinitionKey());
        if (flowTaskExt != null) {
            if (StrUtil.isNotBlank(flowTaskExt.getOperationListJson())) {
                taskInfoVo.setOperationList(JSON.parseArray(flowTaskExt.getOperationListJson(), JSONObject.class));
            }
            if (StrUtil.isNotBlank(flowTaskExt.getVariableListJson())) {
                taskInfoVo.setVariableList(JSON.parseArray(flowTaskExt.getVariableListJson(), JSONObject.class));
            }
        }
        return ResponseResult.success(taskInfoVo);
    }

    /**
     * 获取第一个提交表单数据的任务信息。
     *
     * @param processInstanceId 流程实例Id。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewInitialHistoricTaskInfo")
    public ResponseResult<TaskInfoVo> viewInitialHistoricTaskInfo(@RequestParam String processInstanceId) {
        String errorMessage;
        List<FlowTaskComment> taskCommentList =
                flowTaskCommentService.getFlowTaskCommentList(processInstanceId);
        if (CollUtil.isEmpty(taskCommentList)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        FlowTaskComment taskComment = taskCommentList.get(0);
        if (ObjectUtil.notEqual(taskComment.getCreateUserId(), TokenData.takeFromRequest().getUserId())) {
            errorMessage = "数据验证失败，当前流程发起人与当前用户不匹配！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        HistoricTaskInstance task = flowApiService.getHistoricTaskInstance(processInstanceId, taskComment.getTaskId());
        if (StrUtil.isBlank(task.getFormKey())) {
            errorMessage = "数据验证失败，指定任务的formKey属性不存在，请重新修改流程图！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TaskInfoVo taskInfo = JSON.parseObject(task.getFormKey(), TaskInfoVo.class);
        taskInfo.setTaskKey(task.getTaskDefinitionKey());
        return ResponseResult.success(taskInfo);
    }

    /**
     * 提交多实例加签。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            多实例任务的上一级任务Id。
     * @param newAssignees      新的加签人列表，多个指派人之间逗号分隔。
     * @return 应答结果。
     */
    @PostMapping("/submitConsign")
    public ResponseResult<Void> submitConsign(
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String taskId,
            @MyRequestBody(required = true) String newAssignees) {
        String errorMessage;
        if (!flowApiService.existActiveProcessInstance(processInstanceId)) {
            errorMessage = "数据验证失败，当前流程实例已经结束，不能执行加签！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        HistoricTaskInstance taskInstance = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
        if (taskInstance == null) {
            errorMessage = "数据验证失败，当前任务不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!StrUtil.equals(taskInstance.getAssignee(), TokenData.takeFromRequest().getLoginName())) {
            errorMessage = "数据验证失败，任务指派人与当前用户不匹配！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        List<Task> activeTaskList = flowApiService.getProcessInstanceActiveTaskList(processInstanceId);
        Task activeMultiInstanceTask = null;
        for (Task activeTask : activeTaskList) {
            Object startTaskId = flowApiService.getTaskVariable(
                    activeTask.getId(), FlowConstant.MULTI_SIGN_START_TASK_VAR);
            if (startTaskId != null && startTaskId.toString().equals(taskId)) {
                activeMultiInstanceTask = activeTask;
                break;
            }
        }
        if (activeMultiInstanceTask == null) {
            errorMessage = "数据验证失败，指定加签任务不存在或已审批完毕！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        flowApiService.submitConsign(taskInstance, activeMultiInstanceTask, newAssignees);
        return ResponseResult.success();
    }

    /**
     * 返回当前用户待办的任务列表。
     *
     * @param processDefinitionKey  流程标识。
     * @param processDefinitionName 流程定义名 (模糊查询)。
     * @param taskName              任务名称 (魔术查询。
     * @param pageParam             分页对象。
     * @return 返回当前用户待办的任务列表。如果指定流程标识，则仅返回该流程的待办任务列表。
     */
    @PostMapping("/listRuntimeTask")
    public ResponseResult<MyPageData<FlowTaskVo>> listRuntimeTask(
            @MyRequestBody String processDefinitionKey,
            @MyRequestBody String processDefinitionName,
            @MyRequestBody String taskName,
            @MyRequestBody(required = true) MyPageParam pageParam) {
        String username = TokenData.takeFromRequest().getLoginName();
        MyPageData<Task> pageData = flowApiService.getTaskListByUserName(
                username, processDefinitionKey, processDefinitionName, taskName, pageParam);
        List<FlowTaskVo> flowTaskVoList = flowApiService.convertToFlowTaskList(pageData.getDataList());
        return ResponseResult.success(MyPageUtil.makeResponseData(flowTaskVoList, pageData.getTotalCount()));
    }

    /**
     * 返回当前用户待办的任务数量。
     *
     * @return 返回当前用户待办的任务数量。
     */
    @PostMapping("/countRuntimeTask")
    public ResponseResult<Long> countRuntimeTask() {
        String username = TokenData.takeFromRequest().getLoginName();
        long totalCount = flowApiService.getTaskCountByUserName(username);
        return ResponseResult.success(totalCount);
    }

    /**
     * 获取当前流程任务的审批列表。
     *
     * @param processInstanceId 当前运行时的流程实例Id。
     * @return 当前流程实例的详情数据。
     */
    @GetMapping("/listFlowTaskComment")
    public ResponseResult<List<FlowTaskCommentVo>> listFlowTaskComment(@RequestParam String processInstanceId) {
        List<FlowTaskComment> flowTaskCommentList =
                flowTaskCommentService.getFlowTaskCommentList(processInstanceId);
        List<FlowTaskCommentVo> resultList = FlowTaskComment.INSTANCE.fromModelList(flowTaskCommentList);
        return ResponseResult.success(resultList);
    }

    /**
     * 获取指定流程定义的流程图。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 流程图。
     */
    @GetMapping("/viewProcessBpmn")
    public ResponseResult<String> viewProcessBpmn(@RequestParam String processDefinitionId) throws IOException {
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = flowApiService.getBpmnModelByDefinitionId(processDefinitionId);
        byte[] xmlBytes = converter.convertToXML(bpmnModel);
        InputStream in = new ByteArrayInputStream(xmlBytes);
        return ResponseResult.success(StreamUtils.copyToString(in, StandardCharsets.UTF_8));
    }

    /**
     * 获取流程图高亮数据。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程图高亮数据。
     */
    @GetMapping("/viewHighlightFlowData")
    public ResponseResult<JSONObject> viewHighlightFlowData(@RequestParam String processInstanceId) {
        HistoricProcessInstance hpi = flowApiService.getHistoricProcessInstance(processInstanceId);
        BpmnModel bpmnModel = flowApiService.getBpmnModelByDefinitionId(hpi.getProcessDefinitionId());
        List<Process> processList = bpmnModel.getProcesses();
        List<FlowElement> flowElementList = new LinkedList<>();
        processList.forEach(p -> flowElementList.addAll(p.getFlowElements()));
        Map<String, String> allSequenceFlowMap = new HashMap<>(16);
        for (FlowElement flowElement : flowElementList) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                String ref = sequenceFlow.getSourceRef();
                String targetRef = sequenceFlow.getTargetRef();
                allSequenceFlowMap.put(ref + targetRef, sequenceFlow.getId());
            }
        }
        Set<String> finishedTaskSet = new LinkedHashSet<>();
        //获取流程实例的历史节点(全部执行过的节点，被拒绝的任务节点将会出现多次)
        List<HistoricActivityInstance> activityInstanceList =
                flowApiService.getHistoricActivityInstanceList(processInstanceId);
        Set<String> finishedTaskSequenceSet = new LinkedHashSet<>();
        for (int i = 0; i < activityInstanceList.size(); i++) {
            HistoricActivityInstance current = activityInstanceList.get(i);
            if (i != activityInstanceList.size() - 1) {
                HistoricActivityInstance next = activityInstanceList.get(i + 1);
                finishedTaskSequenceSet.add(current.getActivityId() + next.getActivityId());
            }
            finishedTaskSet.add(current.getActivityId());
        }
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        finishedTaskSequenceSet.forEach(s -> finishedSequenceFlowSet.add(allSequenceFlowMap.get(s)));
        //获取流程实例当前正在待办的节点
        List<HistoricActivityInstance> unfinishedInstanceList =
                flowApiService.getHistoricUnfinishedInstanceList(processInstanceId);
        Set<String> unfinishedTaskSet = new LinkedHashSet<>();
        for (HistoricActivityInstance unfinishedActivity : unfinishedInstanceList) {
            unfinishedTaskSet.add(unfinishedActivity.getActivityId());
        }
        JSONObject jsonData = new JSONObject();
        jsonData.put("finishedTaskSet", finishedTaskSet);
        jsonData.put("finishedSequenceFlowSet", finishedSequenceFlowSet);
        jsonData.put("unfinishedTaskSet", unfinishedTaskSet);
        return ResponseResult.success(jsonData);
    }

    /**
     * 获取当前用户的已办理的审批任务列表。
     *
     * @param processDefinitionName 流程名。
     * @param beginDate             流程发起开始时间。
     * @param endDate               流程发起结束时间。
     * @param pageParam             分页对象。
     * @return 查询结果应答。
     */
    @PostMapping("/listHistoricTask")
    public ResponseResult<MyPageData<Map<String, Object>>> listHistoricTask(
            @MyRequestBody String processDefinitionName,
            @MyRequestBody String beginDate,
            @MyRequestBody String endDate,
            @MyRequestBody(required = true) MyPageParam pageParam) throws ParseException {
        MyPageData<HistoricTaskInstance> pageData =
                flowApiService.getHistoricTaskInstanceFinishedList(processDefinitionName, beginDate, endDate, pageParam);
        List<Map<String, Object>> resultList = new LinkedList<>();
        pageData.getDataList().forEach(instance -> resultList.add(BeanUtil.beanToMap(instance)));
        List<HistoricTaskInstance> taskInstanceList = pageData.getDataList();
        if (CollUtil.isNotEmpty(taskInstanceList)) {
            Set<String> instanceIdSet = taskInstanceList.stream()
                    .map(HistoricTaskInstance::getProcessInstanceId).collect(Collectors.toSet());
            List<HistoricProcessInstance> instanceList = flowApiService.getHistoricProcessInstanceList(instanceIdSet);
            Map<String, HistoricProcessInstance> instanceMap =
                    instanceList.stream().collect(Collectors.toMap(HistoricProcessInstance::getId, c -> c));
            resultList.forEach(result -> {
                HistoricProcessInstance instance = instanceMap.get(result.get("processInstanceId").toString());
                result.put("processDefinitionKey", instance.getProcessDefinitionKey());
                result.put("processDefinitionName", instance.getProcessDefinitionName());
                result.put("startUser", instance.getStartUserId());
                result.put("businessKey", instance.getBusinessKey());
            });
            Set<String> taskIdSet =
                    taskInstanceList.stream().map(HistoricTaskInstance::getId).collect(Collectors.toSet());
            List<FlowTaskComment> commentList = flowTaskCommentService.getFlowTaskCommentListByTaskIds(taskIdSet);
            Map<String, List<FlowTaskComment>> commentMap =
                    commentList.stream().collect(Collectors.groupingBy(FlowTaskComment::getTaskId));
            resultList.forEach(result -> {
                List<FlowTaskComment> comments = commentMap.get(result.get("id").toString());
                if (CollUtil.isNotEmpty(comments)) {
                    result.put("approvalType", comments.get(0).getApprovalType());
                    comments.remove(0);
                }
            });
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList, pageData.getTotalCount()));
    }

    /**
     * 根据输入参数查询，当前用户的历史流程数据。
     *
     * @param processDefinitionName 流程名。
     * @param beginDate             流程发起开始时间。
     * @param endDate               流程发起结束时间。
     * @param pageParam             分页对象。
     * @return 查询结果应答。
     */
    @PostMapping("/listHistoricProcessInstance")
    public ResponseResult<MyPageData<Map<String, Object>>> listHistoricProcessInstance(
            @MyRequestBody String processDefinitionName,
            @MyRequestBody String beginDate,
            @MyRequestBody String endDate,
            @MyRequestBody(required = true) MyPageParam pageParam) throws ParseException {
        String loginName = TokenData.takeFromRequest().getLoginName();
        MyPageData<HistoricProcessInstance> pageData = flowApiService.getHistoricProcessInstanceList(
                null, processDefinitionName, loginName, beginDate, endDate, pageParam, true);
        List<Map<String, Object>> resultList = new LinkedList<>();
        pageData.getDataList().forEach(instance -> resultList.add(BeanUtil.beanToMap(instance)));
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList, pageData.getTotalCount()));
    }

    /**
     * 根据输入参数查询，所有历史流程数据。
     *
     * @param processDefinitionName 流程名。
     * @param startUser             流程发起用户。
     * @param beginDate             流程发起开始时间。
     * @param endDate               流程发起结束时间。
     * @param pageParam             分页对象。
     * @return 查询结果。
     */
    @PostMapping("/listAllHistoricProcessInstance")
    public ResponseResult<MyPageData<Map<String, Object>>> listAllHistoricProcessInstance(
            @MyRequestBody String processDefinitionName,
            @MyRequestBody String startUser,
            @MyRequestBody String beginDate,
            @MyRequestBody String endDate,
            @MyRequestBody(required = true) MyPageParam pageParam) throws ParseException {
        MyPageData<HistoricProcessInstance> pageData = flowApiService.getHistoricProcessInstanceList(
                null, processDefinitionName, startUser, beginDate, endDate, pageParam, false);
        List<Map<String, Object>> resultList = new LinkedList<>();
        pageData.getDataList().forEach(instance -> resultList.add(BeanUtil.beanToMap(instance)));
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList, pageData.getTotalCount()));
    }

    /**
     * 催办工单，只有流程发起人才可以催办工单。
     *
     * @param workOrderId 工单Id。
     * @return 应答结果。
     */
    @PostMapping("/remindRuntimeTask")
    public ResponseResult<Void> remindRuntimeTask(@MyRequestBody(required = true) Long workOrderId) {
        FlowWorkOrder flowWorkOrder = flowWorkOrderService.getById(workOrderId);
        if (flowWorkOrder == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        String errorMessage;
        if (!flowWorkOrder.getCreateUserId().equals(TokenData.takeFromRequest().getUserId())) {
            errorMessage = "数据验证失败，只有流程发起人才能催办工单!";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (flowWorkOrder.getFlowStatus().equals(FlowTaskStatus.FINISHED)) {
            errorMessage = "数据验证失败，已经结束的流程，不能催办工单！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        flowMessageService.saveNewRemindMessage(flowWorkOrder);
        return ResponseResult.success();
    }

    /**
     * 取消工作流工单，仅当没有进入任何审批流程之前，才可以取消工单。
     *
     * @param workOrderId  工单Id。
     * @param cancelReason 取消原因。
     * @return 应答结果。
     */
    @PostMapping("/cancelWorkOrder")
    public ResponseResult<Void> cancelWorkOrder(
            @MyRequestBody(required = true) Long workOrderId,
            @MyRequestBody(required = true) String cancelReason) {
        FlowWorkOrder flowWorkOrder = flowWorkOrderService.getById(workOrderId);
        if (flowWorkOrder == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        String errorMessage;
        if (!flowWorkOrder.getFlowStatus().equals(FlowTaskStatus.SUBMITTED)) {
            errorMessage = "数据验证失败，当前流程已经进入审批状态，不能撤销工单！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!flowWorkOrder.getCreateUserId().equals(TokenData.takeFromRequest().getUserId())) {
            errorMessage = "数据验证失败，当前用户不是工单所有者，不能撤销工单！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        CallResult result = flowApiService.stopProcessInstance(
                flowWorkOrder.getProcessInstanceId(), cancelReason, true);
        if (!result.isSuccess()) {
            return ResponseResult.errorFrom(result);
        }
        return ResponseResult.success();
    }

    /**
     * 终止流程实例，将任务从当前节点直接流转到主流程的结束事件。
     *
     * @param processInstanceId 流程实例Id。
     * @param stopReason        停止原因。
     * @return 执行结果应答。
     */
    @PostMapping("/stopProcessInstance")
    public ResponseResult<Void> stopProcessInstance(
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String stopReason) {
        CallResult result = flowApiService.stopProcessInstance(processInstanceId, stopReason, false);
        if (!result.isSuccess()) {
            return ResponseResult.errorFrom(result);
        }
        return ResponseResult.success();
    }

    /**
     * 删除流程实例。
     *
     * @param processInstanceId 流程实例Id。
     * @return 执行结果应答。
     */
    @PostMapping("/deleteProcessInstance")
    public ResponseResult<Void> deleteProcessInstance(@MyRequestBody(required = true) String processInstanceId) {
        flowApiService.deleteProcessInstance(processInstanceId);
        return ResponseResult.success();
    }
}
