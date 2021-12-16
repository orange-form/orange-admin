package com.orangeforms.common.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.orangeforms.common.flow.object.FlowTaskMultiSignAssign;
import com.orangeforms.common.flow.object.FlowTaskOperation;
import com.orangeforms.common.flow.object.FlowTaskPostCandidateGroup;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.object.MyPageData;
import com.orangeforms.common.core.object.MyPageParam;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.flow.command.AddMultiInstanceExecutionCmd;
import com.orangeforms.common.flow.constant.FlowConstant;
import com.orangeforms.common.flow.constant.FlowApprovalType;
import com.orangeforms.common.flow.constant.FlowTaskStatus;
import com.orangeforms.common.flow.model.FlowEntryPublish;
import com.orangeforms.common.flow.model.FlowTaskComment;
import com.orangeforms.common.flow.model.FlowTaskExt;
import com.orangeforms.common.flow.service.*;
import com.orangeforms.common.flow.util.BaseFlowIdentityExtHelper;
import com.orangeforms.common.flow.util.FlowCustomExtFactory;
import com.orangeforms.common.flow.vo.FlowTaskVo;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("flowApiService")
public class FlowApiServiceImpl implements FlowApiService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private FlowEntryService flowEntryService;
    @Autowired
    private FlowTaskCommentService flowTaskCommentService;
    @Autowired
    private FlowTaskExtService flowTaskExtService;
    @Autowired
    private FlowWorkOrderService flowWorkOrderService;
    @Autowired
    private FlowMessageService flowMessageService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProcessInstance start(String processDefinitionId, Object dataId) {
        String loginName = TokenData.takeFromRequest().getLoginName();
        Map<String, Object> variableMap = new HashMap<>(4);
        variableMap.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, loginName);
        variableMap.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, loginName);
        Authentication.setAuthenticatedUserId(loginName);
        String businessKey = dataId == null ? null : dataId.toString();
        return runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variableMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProcessInstance startAndTakeFirst(
            String processDefinitionId, Object dataId, FlowTaskComment flowTaskComment, JSONObject taskVariableData) {
        String loginName = TokenData.takeFromRequest().getLoginName();
        Authentication.setAuthenticatedUserId(loginName);
        // 设置流程变量。
        Map<String, Object> variableMap = this.initAndGetProcessInstanceVariables(processDefinitionId);
        // 根据当前流程的主版本，启动一个流程实例，同时将businessKey参数设置为主表主键值。
        ProcessInstance instance = runtimeService.startProcessInstanceById(
                processDefinitionId, dataId.toString(), variableMap);
        // 获取流程启动后的第一个任务。
        Task task = taskService.createTaskQuery().processInstanceId(instance.getId()).active().singleResult();
        if (StrUtil.equalsAny(task.getAssignee(), loginName, FlowConstant.START_USER_NAME_VAR)) {
            // 按照规则，调用该方法的用户，就是第一个任务的assignee，因此默认会自动执行complete。
            flowTaskComment.fillWith(task);
            this.completeTask(task, flowTaskComment, taskVariableData);
        }
        return instance;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitConsign(HistoricTaskInstance startTaskInstance, Task multiInstanceActiveTask, String newAssignees) {
        JSONArray assigneeArray = JSON.parseArray(newAssignees);
        for (int i = 0; i < assigneeArray.size(); i++) {
            managementService.executeCommand(new AddMultiInstanceExecutionCmd(
                    startTaskInstance.getId(), multiInstanceActiveTask.getId(), assigneeArray.getString(i)));
        }
        FlowTaskComment flowTaskComment = new FlowTaskComment();
        flowTaskComment.fillWith(startTaskInstance);
        flowTaskComment.setApprovalType(FlowApprovalType.MULTI_CONSIGN);
        String loginName = TokenData.takeFromRequest().getLoginName();
        String comment = String.format("用户 [%s] 加签 [%s]。", loginName, newAssignees);
        flowTaskComment.setComment(comment);
        flowTaskCommentService.saveNew(flowTaskComment);
        return;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeTask(Task task, FlowTaskComment flowTaskComment, JSONObject taskVariableData) {
        if (flowTaskComment != null) {
            // 这里处理多实例会签逻辑。
            if (flowTaskComment.getApprovalType().equals(FlowApprovalType.MULTI_SIGN)) {
                String loginName = TokenData.takeFromRequest().getLoginName();
                if (taskVariableData == null) {
                    taskVariableData = new JSONObject();
                }
                String assigneeList = taskVariableData.getString(FlowConstant.MULTI_ASSIGNEE_LIST_VAR);
                if (StrUtil.isBlank(assigneeList)) {
                    FlowTaskExt flowTaskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(
                            task.getProcessDefinitionId(), task.getTaskDefinitionKey());
                    assigneeList = this.buildMutiSignAssigneeList(flowTaskExt.getOperationListJson());
                    if (assigneeList != null) {
                        taskVariableData.put(FlowConstant.MULTI_ASSIGNEE_LIST_VAR, StrUtil.split(assigneeList,','));
                    }
                }
                Assert.isTrue(StrUtil.isNotBlank(assigneeList));
                taskVariableData.put(FlowConstant.MULTI_AGREE_COUNT_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_SIGN_NUM_OF_INSTANCES_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_SIGN_START_TASK_VAR, task.getId());
                String comment = String.format("用户 [%s] 会签 [%s]。", loginName, assigneeList);
                flowTaskComment.setComment(comment);
            }
            // 处理转办。
            if (FlowApprovalType.TRANSFER.equals(flowTaskComment.getApprovalType())) {
                taskService.setAssignee(task.getId(), flowTaskComment.getDelegateAssginee());
                flowTaskComment.fillWith(task);
                flowTaskCommentService.saveNew(flowTaskComment);
                return;
            }
            if (taskVariableData == null) {
                taskVariableData = new JSONObject();
            }
            this.handleMultiInstanceApprovalType(
                    task.getExecutionId(), flowTaskComment.getApprovalType(), taskVariableData);
            taskVariableData.put(FlowConstant.OPERATION_TYPE_VAR, flowTaskComment.getApprovalType());
            taskService.complete(task.getId(), taskVariableData, true);
            flowTaskComment.fillWith(task);
            flowTaskCommentService.saveNew(flowTaskComment);
        } else {
            taskService.complete(task.getId(), taskVariableData, true);
        }
        flowMessageService.updateFinishedStatusByTaskId(task.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CallResult verifyAssigneeOrCandidateAndClaim(Task task) {
        String errorMessage;
        String loginName = TokenData.takeFromRequest().getLoginName();
        // 这里必须先执行拾取操作，如果当前用户是候选人，特别是对于分布式场景，更是要先完成候选人的拾取。
        if (task.getAssignee() == null) {
            // 没有指派人
            if (!this.isAssigneeOrCandidate(task)) {
                errorMessage = "数据验证失败，当前用户不是该待办任务的候选人，请刷新后重试！";
                return CallResult.error(errorMessage);
            }
            // 作为候选人主动拾取任务。
            taskService.claim(task.getId(), loginName);
        } else {
            if (!task.getAssignee().equals(loginName)) {
                errorMessage = "数据验证失败，当前用户不是该待办任务的指派人，请刷新后重试！";
                return CallResult.error(errorMessage);
            }
        }
        return CallResult.ok();
    }

    @Override
    public Map<String, Object> initAndGetProcessInstanceVariables(String processDefinitionId) {
        TokenData tokenData = TokenData.takeFromRequest();
        String loginName = tokenData.getLoginName();
        // 设置流程变量。
        Map<String, Object> variableMap = new HashMap<>(4);
        variableMap.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, loginName);
        variableMap.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, loginName);
        List<FlowTaskExt> flowTaskExtList = flowTaskExtService.getByProcessDefinitionId(processDefinitionId);
        boolean hasDeptPostLeader = false;
        boolean hasUpDeptPostLeader = false;
        boolean hasPostCandidateGroup = false;
        for (FlowTaskExt flowTaskExt : flowTaskExtList) {
            if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER)) {
                hasUpDeptPostLeader = true;
            } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER)) {
                hasDeptPostLeader = true;
            } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_POST)) {
                hasPostCandidateGroup = true;
            }
        }
        // 如果流程图的配置中包含用户身份相关的变量(如：部门领导和上级领导审批)，flowIdentityExtHelper就不能为null。
        // 这个需要子类去实现 BaseFlowIdentityExtHelper 接口，并注册到FlowCustomExtFactory的工厂中。
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        if (hasUpDeptPostLeader) {
            Assert.notNull(flowIdentityExtHelper);
            Object upLeaderDeptPostId = flowIdentityExtHelper.getUpLeaderDeptPostId(tokenData.getDeptId());
            if (upLeaderDeptPostId != null) {
                variableMap.put(FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR, upLeaderDeptPostId.toString());
            }
        }
        if (hasDeptPostLeader) {
            Assert.notNull(flowIdentityExtHelper);
            Object leaderDeptPostId = flowIdentityExtHelper.getLeaderDeptPostId(tokenData.getDeptId());
            if (leaderDeptPostId != null) {
                variableMap.put(FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR, leaderDeptPostId.toString());
            }
        }
        if (hasPostCandidateGroup) {
            Assert.notNull(flowIdentityExtHelper);
            Map<String, Object> postGroupDataMap =
                    this.buildPostCandidateGroupData(flowIdentityExtHelper, flowTaskExtList);
            variableMap.putAll(postGroupDataMap);
        }
        return variableMap;
    }

    private Map<String, Object> buildPostCandidateGroupData(
            BaseFlowIdentityExtHelper flowIdentityExtHelper, List<FlowTaskExt> flowTaskExtList) {
        Map<String, Object> postVariableMap = new HashMap<>();
        Set<String> selfPostIdSet = new HashSet<>();
        Set<String> upPostIdSet = new HashSet<>();
        for (FlowTaskExt flowTaskExt : flowTaskExtList) {
            if (flowTaskExt.getGroupType().equals(FlowConstant.GROUP_TYPE_POST)) {
                Assert.notNull(flowTaskExt.getDeptPostListJson());
                List<FlowTaskPostCandidateGroup> groupDataList =
                        JSONArray.parseArray(flowTaskExt.getDeptPostListJson(), FlowTaskPostCandidateGroup.class);
                for (FlowTaskPostCandidateGroup groupData : groupDataList) {
                    if (groupData.getType().equals(FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR)) {
                        selfPostIdSet.add(groupData.getPostId());
                    } else if (groupData.getType().equals(FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR)) {
                        upPostIdSet.add(groupData.getPostId());
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(selfPostIdSet)) {
            Map<String, String> deptPostIdMap =
                    flowIdentityExtHelper.getDeptPostIdMap(TokenData.takeFromRequest().getDeptId(), selfPostIdSet);
            for (String postId : selfPostIdSet) {
                if (MapUtil.isNotEmpty(deptPostIdMap) && deptPostIdMap.containsKey(postId)) {
                    String deptPostId = deptPostIdMap.get(postId);
                    postVariableMap.put(FlowConstant.SELF_DEPT_POST_PREFIX + postId, deptPostId);
                } else {
                    postVariableMap.put(FlowConstant.SELF_DEPT_POST_PREFIX + postId, "");
                }
            }
        }
        if (CollUtil.isNotEmpty(upPostIdSet)) {
            Map<String, String> upDeptPostIdMap =
                    flowIdentityExtHelper.getUpDeptPostIdMap(TokenData.takeFromRequest().getDeptId(), upPostIdSet);
            for (String postId : upPostIdSet) {
                if (MapUtil.isNotEmpty(upDeptPostIdMap) && upDeptPostIdMap.containsKey(postId)) {
                    String upDeptPostId = upDeptPostIdMap.get(postId);
                    postVariableMap.put(FlowConstant.UP_DEPT_POST_PREFIX + postId, upDeptPostId);
                } else {
                    postVariableMap.put(FlowConstant.UP_DEPT_POST_PREFIX + postId, "");
                }
            }
        }
        return postVariableMap;
    }

    @Override
    public boolean isAssigneeOrCandidate(TaskInfo task) {
        String loginName = TokenData.takeFromRequest().getLoginName();
        if (StrUtil.isNotBlank(task.getAssignee())) {
            return StrUtil.equals(loginName, task.getAssignee());
        }
        TaskQuery query = taskService.createTaskQuery();
        this.buildCandidateCondition(query, loginName);
        return query.active().count() != 0;
    }

    @Override
    public Collection<FlowElement> getProcessAllElements(String processDefinitionId) {
        Process process = repositoryService.getBpmnModel(processDefinitionId).getProcesses().get(0);
        return this.getAllElements(process.getFlowElements(), null);
    }

    @Override
    public boolean isProcessInstanceStarter(String processInstanceId) {
        String loginName = TokenData.takeFromRequest().getLoginName();
        return historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).startedBy(loginName).count() != 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setBusinessKeyForProcessInstance(String processInstanceId, Object dataId) {
        runtimeService.updateBusinessKey(processInstanceId, dataId.toString());
    }

    @Override
    public boolean existActiveProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).active().count() != 0;
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public Task getProcessInstanceActiveTask(String processInstanceId, String taskId) {
        TaskQuery query = taskService.createTaskQuery().processInstanceId(processInstanceId);
        if (StrUtil.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        return query.active().singleResult();
    }

    @Override
    public List<Task> getProcessInstanceActiveTaskList(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    }

    @Override
    public Task getTaskById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    @Override
    public MyPageData<Task> getTaskListByUserName(
            String username, String definitionKey, String definitionName, String taskName, MyPageParam pageParam) {
        TaskQuery query = taskService.createTaskQuery().active();
        if (StrUtil.isNotBlank(definitionKey)) {
            query.processDefinitionKey(definitionKey);
        }
        if (StrUtil.isNotBlank(definitionName)) {
            query.processDefinitionNameLike("%" + definitionName + "%");
        }
        if (StrUtil.isNotBlank(taskName)) {
            query.taskNameLike("%" + taskName + "%");
        }
        this.buildCandidateCondition(query, username);
        long totalCount = query.count();
        query.orderByTaskCreateTime().desc();
        int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        List<Task> taskList = query.listPage(firstResult, pageParam.getPageSize());
        return new MyPageData<>(taskList, totalCount);
    }

    @Override
    public long getTaskCountByUserName(String username) {
        return taskService.createTaskQuery().taskCandidateOrAssigned(username).active().count();
    }

    @Override
    public List<Task> getTaskListByProcessInstanceIds(List<String> processInstanceIdSet) {
        return taskService.createTaskQuery().processInstanceIdIn(processInstanceIdSet).active().list();
    }

    @Override
    public List<ProcessInstance> getProcessInstanceList(Set<String> processInstanceIdSet) {
        return runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();
    }

    @Override
    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionList(Set<String> processDefinitionIdSet) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefinitionIdSet).list();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void suspendProcessDefinition(String processDefinitionId) {
        repositoryService.suspendProcessDefinitionById(processDefinitionId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void activateProcessDefinition(String processDefinitionId) {
        repositoryService.activateProcessDefinitionById(processDefinitionId);
    }

    @Override
    public BpmnModel getBpmnModelByDefinitionId(String processDefinitionId) {
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    @Override
    public ProcessDefinition getProcessDefinitionByDeployId(String deployId) {
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
    }

    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String variableName) {
        return runtimeService.getVariable(processInstanceId, variableName);
    }

    @Override
    public List<FlowTaskVo> convertToFlowTaskList(List<Task> taskList) {
        List<FlowTaskVo> flowTaskVoList = new LinkedList<>();
        if (CollUtil.isEmpty(taskList)) {
            return flowTaskVoList;
        }
        Set<String> processDefinitionIdSet = taskList.stream()
                .map(Task::getProcessDefinitionId).collect(Collectors.toSet());
        Set<String> procInstanceIdSet = taskList.stream()
                .map(Task::getProcessInstanceId).collect(Collectors.toSet());
        List<FlowEntryPublish> flowEntryPublishList =
                flowEntryService.getFlowEntryPublishList(processDefinitionIdSet);
        Map<String, FlowEntryPublish> flowEntryPublishMap =
                flowEntryPublishList.stream().collect(Collectors.toMap(FlowEntryPublish::getProcessDefinitionId, c -> c));
        List<ProcessInstance> instanceList = this.getProcessInstanceList(procInstanceIdSet);
        Map<String, ProcessInstance> instanceMap =
                instanceList.stream().collect(Collectors.toMap(ProcessInstance::getId, c -> c));
        List<ProcessDefinition> definitionList = this.getProcessDefinitionList(processDefinitionIdSet);
        Map<String, ProcessDefinition> definitionMap =
                definitionList.stream().collect(Collectors.toMap(ProcessDefinition::getId, c -> c));
        for (Task task : taskList) {
            FlowTaskVo flowTaskVo = new FlowTaskVo();
            flowTaskVo.setTaskId(task.getId());
            flowTaskVo.setTaskName(task.getName());
            flowTaskVo.setTaskKey(task.getTaskDefinitionKey());
            flowTaskVo.setTaskFormKey(task.getFormKey());
            flowTaskVo.setEntryId(flowEntryPublishMap.get(task.getProcessDefinitionId()).getEntryId());
            ProcessDefinition processDefinition = definitionMap.get(task.getProcessDefinitionId());
            flowTaskVo.setProcessDefinitionId(processDefinition.getId());
            flowTaskVo.setProcessDefinitionName(processDefinition.getName());
            flowTaskVo.setProcessDefinitionKey(processDefinition.getKey());
            flowTaskVo.setProcessDefinitionVersion(processDefinition.getVersion());
            ProcessInstance processInstance = instanceMap.get(task.getProcessInstanceId());
            flowTaskVo.setProcessInstanceId(processInstance.getId());
            Object initiator = this.getProcessInstanceVariable(
                    processInstance.getId(), FlowConstant.PROC_INSTANCE_INITIATOR_VAR);
            flowTaskVo.setProcessInstanceInitiator(initiator.toString());
            flowTaskVo.setProcessInstanceStartTime(processInstance.getStartTime());
            flowTaskVo.setBusinessKey(processInstance.getBusinessKey());
            flowTaskVoList.add(flowTaskVo);
        }
        return flowTaskVoList;
    }

    @Override
    public void addProcessInstanceEndListener(BpmnModel bpmnModel, Class<? extends ExecutionListener> listenerClazz) {
        Assert.notNull(listenerClazz);
        Process process = bpmnModel.getMainProcess();
        ActivitiListener activitiListener = new ActivitiListener();
        activitiListener.setEvent("end");
        activitiListener.setImplementationType("class");
        activitiListener.setImplementation(listenerClazz.getName());
        process.getExecutionListeners().add(activitiListener);
    }

    @Override
    public void addTaskCreateListener(UserTask userTask, Class<? extends TaskListener> listenerClazz) {
        Assert.notNull(listenerClazz);
        ActivitiListener activitiListener = new ActivitiListener();
        activitiListener.setEvent("create");
        activitiListener.setImplementationType("class");
        activitiListener.setImplementation(listenerClazz.getName());
        userTask.getTaskListeners().add(activitiListener);
    }

    @Override
    public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public List<HistoricProcessInstance> getHistoricProcessInstanceList(Set<String> processInstanceIdSet) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();
    }

    @Override
    public MyPageData<HistoricProcessInstance> getHistoricProcessInstanceList(
            String processDefinitionKey,
            String processDefinitionName,
            String startUser,
            String beginDate,
            String endDate,
            MyPageParam pageParam,
            boolean finishedOnly) throws ParseException {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (StrUtil.isNotBlank(processDefinitionKey)) {
            query.processDefinitionKey(processDefinitionKey);
        }
        if (StrUtil.isNotBlank(processDefinitionName)) {
            query.processDefinitionName(processDefinitionName);
        }
        if (StrUtil.isNotBlank(startUser)) {
            query.startedBy(startUser);
        }
        if (StrUtil.isNotBlank(beginDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.startedAfter(sdf.parse(beginDate));
        }
        if (StrUtil.isNotBlank(endDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.startedBefore(sdf.parse(endDate));
        }
        if (finishedOnly) {
            query.finished();
        }
        query.orderByProcessInstanceStartTime().desc();
        long totalCount = query.count();
        int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        List<HistoricProcessInstance> instanceList = query.listPage(firstResult, pageParam.getPageSize());
        return new MyPageData<>(instanceList, totalCount);
    }

    @Override
    public MyPageData<HistoricTaskInstance> getHistoricTaskInstanceFinishedList(
            String processDefinitionName,
            String beginDate,
            String endDate,
            MyPageParam pageParam) throws ParseException {
        String loginName = TokenData.takeFromRequest().getLoginName();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(loginName)
                .finished();
        if (StrUtil.isNotBlank(processDefinitionName)) {
            query.processDefinitionName(processDefinitionName);
        }
        if (StrUtil.isNotBlank(beginDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.taskCompletedAfter(sdf.parse(beginDate));
        }
        if (StrUtil.isNotBlank(endDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.taskCompletedBefore(sdf.parse(endDate));
        }
        query.orderByHistoricTaskInstanceEndTime().desc();
        long totalCount = query.count();
        int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        List<HistoricTaskInstance> instanceList = query.listPage(firstResult, pageParam.getPageSize());
        return new MyPageData<>(instanceList, totalCount);
    }

    @Override
    public List<HistoricActivityInstance> getHistoricActivityInstanceList(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
    }

    @Override
    public List<HistoricActivityInstance> getHistoricActivityInstanceListOrderByStartTime(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
    }

    @Override
    public HistoricTaskInstance getHistoricTaskInstance(String processInstanceId, String taskId) {
        return historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).taskId(taskId).singleResult();
    }

    @Override
    public List<HistoricActivityInstance> getHistoricUnfinishedInstanceList(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).unfinished().list();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CallResult stopProcessInstance(String processInstanceId, String stopReason, boolean forCancel) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        if (CollUtil.isEmpty(taskList)) {
            return CallResult.error("数据验证失败，当前流程尚未开始或已经结束！");
        }
        for (Task task : taskList) {
            String currActivityId = task.getTaskDefinitionKey();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            FlowNode currFlow = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currActivityId);
            if (currFlow == null) {
                List<SubProcess> subProcessList =
                        bpmnModel.getMainProcess().findFlowElementsOfType(SubProcess.class);
                for (SubProcess subProcess : subProcessList) {
                    FlowElement flowElement = subProcess.getFlowElement(currActivityId);
                    if (flowElement != null) {
                        currFlow = (FlowNode) flowElement;
                        break;
                    }
                }
            }
            EndEvent endEvent = bpmnModel.getMainProcess()
                    .findFlowElementsOfType(EndEvent.class, false).get(0);
            if (!(currFlow.getParentContainer().equals(endEvent.getParentContainer()))) {
                return CallResult.error("数据验证失败，不能从子流程直接中止！");
            }
            // 保存原有的输出方向。
            List<SequenceFlow> oriSequenceFlows = Lists.newArrayList();
            oriSequenceFlows.addAll(currFlow.getOutgoingFlows());
            // 清空原有方向。
            currFlow.getOutgoingFlows().clear();
            // 建立新方向。
            SequenceFlow newSequenceFlow = new SequenceFlow();
            String uuid = UUID.randomUUID().toString().replace("-", "");
            newSequenceFlow.setId(uuid);
            newSequenceFlow.setSourceFlowElement(currFlow);
            newSequenceFlow.setTargetFlowElement(endEvent);
            currFlow.setOutgoingFlows(CollUtil.newArrayList(newSequenceFlow));
            // 完成任务并跳转到新方向。
            taskService.complete(task.getId());
            FlowTaskComment taskComment = new FlowTaskComment(task);
            taskComment.setApprovalType(FlowApprovalType.STOP);
            taskComment.setComment(stopReason);
            flowTaskCommentService.saveNew(taskComment);
            // 回复原有输出方向。
            currFlow.setOutgoingFlows(oriSequenceFlows);
        }
        int status = FlowTaskStatus.STOPPED;
        if (forCancel) {
            status = FlowTaskStatus.CANCELLED;
        }
        flowWorkOrderService.updateFlowStatusByProcessInstanceId(processInstanceId, status);
        flowMessageService.updateFinishedStatusByProcessInstanceId(processInstanceId);
        return CallResult.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProcessInstance(String processInstanceId) {
        historyService.deleteHistoricProcessInstance(processInstanceId);
        flowWorkOrderService.removeByProcessInstanceId(processInstanceId);
    }

    @Override
    public Object getTaskVariable(String taskId, String variableName) {
        return taskService.getVariable(taskId, variableName);
    }

    @Override
    public BpmnModel convertToBpmnModel(String bpmnXml) throws XMLStreamException {
        BpmnXMLConverter converter = new BpmnXMLConverter();
        InputStream in = new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8));
        @Cleanup XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
        return converter.convertToBpmnModel(reader);
    }

    private void handleMultiInstanceApprovalType(String executionId, String approvalType, JSONObject taskVariableData) {
        if (StrUtil.isBlank(approvalType)) {
            return;
        }
        if (StrUtil.equalsAny(approvalType,
                FlowApprovalType.MULTI_AGREE,
                FlowApprovalType.MULTI_REFUSE,
                FlowApprovalType.MULTI_ABSTAIN)) {
            Map<String, Object> variables = runtimeService.getVariables(executionId);
            Integer agreeCount = (Integer) variables.get(FlowConstant.MULTI_AGREE_COUNT_VAR);
            Integer refuseCount = (Integer) variables.get(FlowConstant.MULTI_REFUSE_COUNT_VAR);
            Integer abstainCount = (Integer) variables.get(FlowConstant.MULTI_ABSTAIN_COUNT_VAR);
            Integer nrOfInstances = (Integer) variables.get(FlowConstant.NUMBER_OF_INSTANCES_VAR);
            taskVariableData.put(FlowConstant.MULTI_AGREE_COUNT_VAR, agreeCount);
            taskVariableData.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, refuseCount);
            taskVariableData.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, abstainCount);
            taskVariableData.put(FlowConstant.MULTI_SIGN_NUM_OF_INSTANCES_VAR, nrOfInstances);
            switch (approvalType) {
                case FlowApprovalType.MULTI_AGREE:
                    if (agreeCount == null) {
                        agreeCount = 0;
                    }
                    taskVariableData.put(FlowConstant.MULTI_AGREE_COUNT_VAR, agreeCount + 1);
                    break;
                case FlowApprovalType.MULTI_REFUSE:
                    if (refuseCount == null) {
                        refuseCount = 0;
                    }
                    taskVariableData.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, refuseCount + 1);
                    break;
                case FlowApprovalType.MULTI_ABSTAIN:
                    if (abstainCount == null) {
                        abstainCount = 0;
                    }
                    taskVariableData.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, abstainCount + 1);
                    break;
                default:
                    break;
            }
        }
    }

    private void buildCandidateCondition(TaskQuery query, String loginName) {
        Set<String> groupIdSet = new HashSet<>();
        // NOTE: 需要注意的是，部门Id、部门岗位Id，或者其他类型的分组Id，他们之间一定不能重复。
        TokenData tokenData = TokenData.takeFromRequest();
        Object deptId = tokenData.getDeptId();
        if (deptId != null) {
            groupIdSet.add(deptId.toString());
        }
        String roleIds = tokenData.getRoleIds();
        if (StrUtil.isNotBlank(tokenData.getRoleIds())) {
            groupIdSet.addAll(Arrays.asList(StrUtil.split(roleIds, ",")));
        }
        String postIds = tokenData.getPostIds();
        if (StrUtil.isNotBlank(tokenData.getPostIds())) {
            groupIdSet.addAll(Arrays.asList(StrUtil.split(postIds, ",")));
        }
        String deptPostIds = tokenData.getDeptPostIds();
        if (StrUtil.isNotBlank(deptPostIds)) {
            groupIdSet.addAll(Arrays.asList(StrUtil.split(deptPostIds, ",")));
        }
        if (CollUtil.isNotEmpty(groupIdSet)) {
            List<String> groupIdList = new LinkedList<>(groupIdSet);
            query.or().taskCandidateGroupIn(groupIdList).taskCandidateOrAssigned(loginName).endOr();
        } else {
            query.taskCandidateOrAssigned(loginName);
        }
    }

    private String buildMutiSignAssigneeList(String operationListJson) {
        FlowTaskMultiSignAssign multiSignAssignee = null;
        List<FlowTaskOperation> taskOperationList = JSONArray.parseArray(operationListJson, FlowTaskOperation.class);
        for (FlowTaskOperation taskOperation : taskOperationList) {
            if ("multi_sign".equals(taskOperation.getType())) {
                multiSignAssignee = taskOperation.getMultiSignAssignee();
                break;
            }
        }
        Assert.notNull(multiSignAssignee);
        if (FlowTaskMultiSignAssign.ASSIGN_TYPE_USER.equals(multiSignAssignee.getAssigneeType())) {
            return multiSignAssignee.getAssigneeList();
        }
        Set<String> usernameSet = null;
        BaseFlowIdentityExtHelper extHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        Set<String> idSet = CollUtil.newHashSet(StrUtil.split(multiSignAssignee.getAssigneeList(), ","));
        switch (multiSignAssignee.getAssigneeType()) {
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_ROLE:
                usernameSet = extHelper.getUsernameListByRoleIds(idSet);
                break;
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_DEPT:
                usernameSet = extHelper.getUsernameListByDeptIds(idSet);
                break;
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_POST:
                usernameSet = extHelper.getUsernameListByPostIds(idSet);
                break;
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_DEPT_POST:
                usernameSet = extHelper.getUsernameListByDeptPostIds(idSet);
                break;
            default:
                break;
        }
        return CollUtil.isEmpty(usernameSet) ? null : CollUtil.join(usernameSet, ",");
    }

    private Collection<FlowElement> getAllElements(Collection<FlowElement> flowElements, Collection<FlowElement> allElements) {
        allElements = allElements == null ? new ArrayList<>() : allElements;
        for (FlowElement flowElement : flowElements) {
            allElements.add(flowElement);
            if (flowElement instanceof SubProcess) {
                allElements = getAllElements(((SubProcess) flowElement).getFlowElements(), allElements);
            }
        }
        return allElements;
    }
}
