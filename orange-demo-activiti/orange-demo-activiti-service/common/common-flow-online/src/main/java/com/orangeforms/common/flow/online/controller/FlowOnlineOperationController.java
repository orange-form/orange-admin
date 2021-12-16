package com.orangeforms.common.flow.online.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.flow.dto.FlowWorkOrderDto;
import com.orangeforms.common.flow.vo.FlowEntryVo;
import com.orangeforms.common.flow.vo.FlowWorkOrderVo;
import com.orangeforms.common.online.dto.OnlineFilterDto;
import com.orangeforms.common.online.model.*;
import com.orangeforms.common.online.model.constant.FieldFilterType;
import com.orangeforms.common.online.model.constant.RelationType;
import com.orangeforms.common.online.object.ColumnData;
import com.orangeforms.common.online.service.OnlineFormService;
import com.orangeforms.common.online.service.OnlinePageService;
import com.orangeforms.common.online.service.OnlineOperationService;
import com.orangeforms.common.online.service.OnlineTableService;
import com.orangeforms.common.online.util.OnlineOperationHelper;
import com.orangeforms.common.flow.online.service.FlowOnlineOperationService;
import com.orangeforms.common.flow.constant.FlowApprovalType;
import com.orangeforms.common.flow.util.FlowOperationHelper;
import com.orangeforms.common.flow.constant.FlowTaskStatus;
import com.orangeforms.common.flow.dto.FlowTaskCommentDto;
import com.orangeforms.common.flow.exception.FlowOperationException;
import com.orangeforms.common.flow.model.*;
import com.orangeforms.common.flow.service.*;
import com.orangeforms.common.flow.vo.TaskInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("${common-flow.urlPrefix}/flowOnlineOperation")
public class FlowOnlineOperationController {

    @Autowired
    private FlowEntryService flowEntryService;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowOperationHelper flowOperationHelper;
    @Autowired
    private FlowOnlineOperationService flowOnlineOperationService;
    @Autowired
    private FlowWorkOrderService flowWorkOrderService;
    @Autowired
    private OnlineFormService onlineFormService;
    @Autowired
    private OnlinePageService onlinePageService;
    @Autowired
    private OnlineOperationService onlineOperationService;
    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlineOperationHelper onlineOperationHelper;

    /**
     * 根据指定流程的主版本，发起一个流程实例，同时作为第一个任务节点的执行人，执行第一个用户任务。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param flowTaskCommentDto   审批意见。
     * @param taskVariableData     流程任务变量数据。
     * @param masterData           流程审批相关的主表数据。
     * @param slaveData            流程审批相关的多个从表数据。
     * @return 应答结果对象。
     */
    @PostMapping("/startAndTakeUserTask/{processDefinitionKey}")
    public ResponseResult<Void> startAndTakeUserTask(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody(required = true) FlowTaskCommentDto flowTaskCommentDto,
            @MyRequestBody JSONObject taskVariableData,
            @MyRequestBody(required = true) JSONObject masterData,
            @MyRequestBody JSONObject slaveData) {
        String errorMessage;
        // 1. 验证流程数据的合法性。
        ResponseResult<FlowEntry> flowEntryResult = flowOperationHelper.verifyAndGetFlowEntry(processDefinitionKey);
        if (!flowEntryResult.isSuccess()) {
            return ResponseResult.errorFrom(flowEntryResult);
        }
        // 2. 验证流程一个用户任务的合法性。
        FlowEntryPublish flowEntryPublish = flowEntryResult.getData().getMainFlowEntryPublish();
        if (!flowEntryPublish.getActiveStatus()) {
            errorMessage = "数据验证失败，当前流程发布对象已被挂起，不能启动新流程！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ResponseResult<TaskInfoVo> taskInfoResult =
                flowOperationHelper.verifyAndGetInitialTaskInfo(flowEntryPublish, true);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        TaskInfoVo taskInfo = taskInfoResult.getData();
        // 3. 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        OnlineTable masterTable = datasource.getMasterTable();
        // 4. 为本次流程操作构建数据。
        ResponseResult<List<ColumnData>> columnDataListResult =
                onlineOperationHelper.buildTableData(masterTable, masterData, false, null);
        if (!columnDataListResult.isSuccess()) {
            return ResponseResult.errorFrom(columnDataListResult);
        }
        FlowTaskComment flowTaskComment = BeanUtil.copyProperties(flowTaskCommentDto, FlowTaskComment.class);
        // 5. 保存在线表单提交的数据，同时启动流程和自动完成第一个用户任务。
        if (slaveData == null) {
            flowOnlineOperationService.saveNewAndStartProcess(
                    flowEntryPublish.getProcessDefinitionId(),
                    flowTaskComment,
                    taskVariableData,
                    masterTable,
                    columnDataListResult.getData());
        } else {
            // 如果本次请求中包含从表数据，则一同插入。
            ResponseResult<Map<OnlineDatasourceRelation, List<List<ColumnData>>>> slaveDataListResult =
                    onlineOperationHelper.buildSlaveDataList(datasource.getDatasourceId(), slaveData);
            if (!slaveDataListResult.isSuccess()) {
                return ResponseResult.errorFrom(slaveDataListResult);
            }
            flowOnlineOperationService.saveNewAndStartProcess(
                    flowEntryPublish.getProcessDefinitionId(),
                    flowTaskComment,
                    taskVariableData,
                    masterTable,
                    columnDataListResult.getData(),
                    slaveDataListResult.getData());
        }
        return ResponseResult.success();
    }

    /**
     * 提交流程的用户任务。
     *
     * @param processInstanceId  流程实例Id。
     * @param taskId             流程任务Id。
     * @param flowTaskCommentDto 流程审批数据。
     * @param taskVariableData   流程任务变量数据。
     * @param masterData         流程审批相关的主表数据。
     * @param slaveData          流程审批相关的多个从表数据。
     * @return 应答结果对象。
     */
    @PostMapping("/submitUserTask")
    public ResponseResult<Void> submitUserTask(
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String taskId,
            @MyRequestBody(required = true) FlowTaskCommentDto flowTaskCommentDto,
            @MyRequestBody JSONObject taskVariableData,
            @MyRequestBody JSONObject masterData,
            @MyRequestBody JSONObject slaveData) {
        String errorMessage;
        // 验证流程任务的合法性。
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        ResponseResult<TaskInfoVo> taskInfoResult = flowOperationHelper.verifyAndGetRuntimeTaskInfo(task);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        TaskInfoVo taskInfo = taskInfoResult.getData();
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        CallResult assigneeVerifyResult = flowApiService.verifyAssigneeOrCandidateAndClaim(task);
        if (!assigneeVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(assigneeVerifyResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        OnlineTable masterTable = datasource.getMasterTable();
        Long datasourceId = datasource.getDatasourceId();
        String dataId = task.getBusinessKey();
        FlowTaskComment flowTaskComment = BeanUtil.copyProperties(flowTaskCommentDto, FlowTaskComment.class);
        if (StrUtil.isBlank(dataId)) {
            return this.submitNewTask(processInstanceId, taskId,
                    flowTaskComment, taskVariableData, masterTable, masterData, slaveData, datasourceId);
        }
        try {
            if (StrUtil.equals(flowTaskComment.getApprovalType(), FlowApprovalType.TRANSFER)) {
                if (StrUtil.isBlank(flowTaskComment.getDelegateAssginee())) {
                    errorMessage = "数据验证失败，加签或转办任务指派人不能为空！！";
                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
                }
            }
            flowOnlineOperationService.updateAndTakeTask(
                    task, flowTaskComment, taskVariableData, masterTable, masterData, dataId, slaveData, datasourceId);
        } catch (FlowOperationException e) {
            log.error("Failed to call [FlowOnlineOperationService.updateAndTakeTask]", e);
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, e.getMessage());
        }
        return ResponseResult.success();
    }

    /**
     * 获取当前流程实例的详情数据。包括主表数据、一对一从表数据、一对多从表数据列表等。
     *
     * @param processInstanceId 当前运行时的流程实例Id。
     * @param taskId            流程任务Id。
     * @return 当前流程实例的详情数据。
     */
    @GetMapping("/viewUserTask")
    public ResponseResult<JSONObject> viewUserTask(@RequestParam String processInstanceId, @RequestParam String taskId) {
        String errorMessage;
        // 验证流程任务的合法性。
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        // 如果业务主数据为空，则直接返回。
        if (StrUtil.isBlank(task.getBusinessKey())) {
            return ResponseResult.success(null);
        }
        ResponseResult<TaskInfoVo> taskInfoResult = flowOperationHelper.verifyAndGetRuntimeTaskInfo(task);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        TaskInfoVo taskInfo = taskInfoResult.getData();
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasource.getDatasourceId(), null);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        JSONObject jsonData = this.buildUserTaskData(
                task.getBusinessKey(), datasource.getMasterTable(), relationListResult.getData());
        return ResponseResult.success(jsonData);
    }

    /**
     * 获取已经结束的流程实例的详情数据。包括主表数据、一对一从表数据、一对多从表数据列表等。
     *
     * @param processInstanceId 历史流程实例Id。
     * @param taskId            历史任务Id。如果该值为null，仅有发起人可以查看当前流程数据，否则只有任务的指派人才能查看。
     * @return 历史流程实例的详情数据。
     */
    @GetMapping("/viewHistoricProcessInstance")
    public ResponseResult<JSONObject> viewHistoricProcessInstance(
            @RequestParam String processInstanceId, @RequestParam(required = false) String taskId) {
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
        if (StrUtil.isBlank(instance.getBusinessKey())) {
            // 对于没有提交过任何用户任务的场景，可直接返回空数据。
            return ResponseResult.success(new JSONObject());
        }
        FlowEntryPublish flowEntryPublish =
                flowEntryService.getFlowEntryPublishList(CollUtil.newHashSet(instance.getProcessDefinitionId())).get(0);
        TaskInfoVo taskInfoVo = JSON.parseObject(flowEntryPublish.getInitTaskInfo(), TaskInfoVo.class);
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfoVo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasource.getDatasourceId(), null);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        JSONObject jsonData = this.buildUserTaskData(
                instance.getBusinessKey(), datasource.getMasterTable(), relationListResult.getData());
        return ResponseResult.success(jsonData);
    }

    /**
     * 工作流工单列表。
     *
     * @param processDefinitionKey   流程标识名。
     * @param flowWorkOrderDtoFilter 过滤对象。
     * @param pageParam              分页参数。
     * @return 查询结果。
     */
    @PostMapping("/listWorkOrder/{processDefinitionKey}")
    public ResponseResult<MyPageData<FlowWorkOrderVo>> listWorkOrder(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody FlowWorkOrderDto flowWorkOrderDtoFilter,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        FlowWorkOrder flowWorkOrderFilter =
                flowOperationHelper.makeWorkOrderFilter(flowWorkOrderDtoFilter, processDefinitionKey);
        MyOrderParam orderParam = new MyOrderParam();
        orderParam.add(new MyOrderParam.OrderInfo("workOrderId", false, null));
        String orderBy = MyOrderParam.buildOrderBy(orderParam, FlowWorkOrder.class);
        List<FlowWorkOrder> flowWorkOrderList = flowWorkOrderService.getFlowWorkOrderList(flowWorkOrderFilter, orderBy);
        MyPageData<FlowWorkOrderVo> resultData =
                MyPageUtil.makeResponseData(flowWorkOrderList, FlowWorkOrder.INSTANCE);
        ResponseResult<Void> responseResult = this.makeWorkOrderTaskInfo(resultData.getDataList());
        if (!responseResult.isSuccess()) {
            return ResponseResult.errorFrom(responseResult);
        }
        return ResponseResult.success(resultData);
    }

    /**
     * 为数据源主表字段上传文件。
     *
     * @param processDefinitionKey 流程引擎流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @param taskId               流程任务Id。
     * @param datasourceId         数据源Id。
     * @param relationId           数据源关联Id。
     * @param fieldName            数据表字段名。
     * @param asImage              是否为图片文件。
     * @param uploadFile           上传文件对象。
     */
    @PostMapping("/upload")
    public void upload(
            @RequestParam String processDefinitionKey,
            @RequestParam(required = false) String processInstanceId,
            @RequestParam(required = false) String taskId,
            @RequestParam Long datasourceId,
            @RequestParam(required = false) Long relationId,
            @RequestParam String fieldName,
            @RequestParam Boolean asImage,
            @RequestParam("uploadFile") MultipartFile uploadFile) throws Exception {
        ResponseResult<String> verifyResult =
                this.verifyUploadOrDownload(processDefinitionKey, processInstanceId, taskId, datasourceId);
        if (!verifyResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyResult));
            return;
        }
        ResponseResult<OnlineTable> verifyTableResult =
                this.verifyAndGetOnlineTable(datasourceId, relationId, null, null);
        if (!verifyTableResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyTableResult));
            return;
        }
        onlineOperationHelper.doUpload(verifyTableResult.getData(), fieldName, asImage, uploadFile);
    }

    /**
     * 下载文件接口。
     * 越权访问限制说明：
     * taskId为空，当前用户必须为当前流程的发起人，否则必须为当前任务的指派人或候选人。
     * relationId为空，下载数据为主表字段，否则为关联的从表字段。
     *
     * @param processDefinitionKey 流程引擎流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @param taskId               流程任务Id。
     * @param datasourceId         数据源Id。
     * @param relationId           数据源关联Id。
     * @param dataId               附件所在记录的主键Id。
     * @param fieldName            数据表字段名。
     * @param asImage              是否为图片文件。
     * @param response             Http 应答对象。
     */
    @GetMapping("/download")
    public void download(
            @RequestParam String processDefinitionKey,
            @RequestParam(required = false) String processInstanceId,
            @RequestParam(required = false) String taskId,
            @RequestParam Long datasourceId,
            @RequestParam(required = false) Long relationId,
            @RequestParam(required = false) String dataId,
            @RequestParam String fieldName,
            @RequestParam String filename,
            @RequestParam Boolean asImage,
            HttpServletResponse response) throws Exception {
        ResponseResult<String> verifyResult =
                this.verifyUploadOrDownload(processDefinitionKey, processInstanceId, taskId, datasourceId);
        if (!verifyResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyResult));
            return;
        }
        ResponseResult<OnlineTable> verifyTableResult =
                this.verifyAndGetOnlineTable(datasourceId, relationId, verifyResult.getData(), dataId);
        if (!verifyTableResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyTableResult));
            return;
        }
        onlineOperationHelper.doDownload(verifyTableResult.getData(), dataId, fieldName, filename, asImage, response);
    }

    /**
     * 获取所有流程对象，同时获取关联的在线表单对象列表。
     *
     * @return 查询结果。
     */
    @GetMapping("/listFlowEntryForm")
    public ResponseResult<List<FlowEntryVo>> listFlowEntryForm() {
        List<FlowEntry> flowEntryList = flowEntryService.getAllList();
        List<FlowEntryVo> flowEntryVoList = FlowEntry.INSTANCE.fromModelList(flowEntryList);
        if (CollUtil.isNotEmpty(flowEntryVoList)) {
            Set<Long> pageIdSet = flowEntryVoList.stream().map(FlowEntryVo::getPageId).collect(Collectors.toSet());
            List<OnlineForm> formList = onlineFormService.getOnlineFormListByPageIds(pageIdSet);
            Map<Long, List<OnlineForm>> formMap =
                    formList.stream().collect(Collectors.groupingBy(OnlineForm::getPageId));
            for (FlowEntryVo flowEntryVo : flowEntryVoList) {
                List<OnlineForm> flowEntryFormList = formMap.get(flowEntryVo.getPageId());
                flowEntryVo.setFormList(MyModelUtil.beanToMapList(flowEntryFormList));
            }
        }
        return ResponseResult.success(flowEntryVoList);
    }

    private ResponseResult<OnlineDatasource> verifyAndGetOnlineDatasource(Long formId) {
        List<OnlineFormDatasource> formDatasourceList = onlineFormService.getFormDatasourceListByFormId(formId);
        if (CollUtil.isEmpty(formDatasourceList)) {
            String errorMessage = "数据验证失败，流程任务绑定的在线表单Id [" + formId + "] 不存在，请修改流程图！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return onlineOperationHelper.verifyAndGetDatasource(formDatasourceList.get(0).getDatasourceId());
    }

    private JSONObject buildUserTaskData(
            String businessKey, OnlineTable masterTable, List<OnlineDatasourceRelation> relationList) {
        JSONObject jsonData = new JSONObject();
        List<OnlineDatasourceRelation> oneToOneRelationList = relationList.stream()
                .filter(r -> r.getRelationType().equals(RelationType.ONE_TO_ONE)).collect(Collectors.toList());
        Map<String, Object> result =
                onlineOperationService.getMasterData(masterTable, oneToOneRelationList, relationList, businessKey);
        if (MapUtil.isEmpty(result)) {
            return jsonData;
        }
        jsonData.put("masterAndOneToOne", result);
        List<OnlineDatasourceRelation> oneToManyRelationList = relationList.stream()
                .filter(r -> r.getRelationType().equals(RelationType.ONE_TO_MANY)).collect(Collectors.toList());
        if (CollUtil.isEmpty(oneToManyRelationList)) {
            return jsonData;
        }
        JSONObject oneToManyJsonData = new JSONObject();
        jsonData.put("oneToMany", oneToManyJsonData);
        for (OnlineDatasourceRelation relation : oneToManyRelationList) {
            OnlineFilterDto filterDto = new OnlineFilterDto();
            filterDto.setTableName(relation.getSlaveTable().getTableName());
            OnlineColumn slaveColumn = relation.getSlaveTable().getColumnMap().get(relation.getSlaveColumnId());
            filterDto.setColumnName(slaveColumn.getColumnName());
            filterDto.setFilterType(FieldFilterType.EQUAL_FILTER);
            Object columnValue = result.get(masterTable.getPrimaryKeyColumn().getColumnName());
            filterDto.setColumnValue(columnValue);
            List<Map<String, Object>> slaveResultList =
                    onlineOperationService.getSlaveDataList(relation, CollUtil.newLinkedList(filterDto), null);
            if (CollUtil.isNotEmpty(slaveResultList)) {
                oneToManyJsonData.put(relation.getVariableName(), slaveResultList);
            }
        }
        return jsonData;
    }

    private ResponseResult<Void> submitNewTask(
            String processInstanceId,
            String taskId,
            FlowTaskComment flowTaskComment,
            JSONObject taskVariableData,
            OnlineTable masterTable,
            JSONObject masterData,
            JSONObject slaveData,
            Long datasourceId) {
        ResponseResult<List<ColumnData>> columnDataListResult =
                onlineOperationHelper.buildTableData(masterTable, masterData, false, null);
        if (!columnDataListResult.isSuccess()) {
            return ResponseResult.errorFrom(columnDataListResult);
        }
        // 保存在线表单提交的数据，同时启动流程和自动完成第一个用户任务。
        if (slaveData == null) {
            flowOnlineOperationService.saveNewAndTakeTask(
                    processInstanceId,
                    taskId,
                    flowTaskComment,
                    taskVariableData,
                    masterTable,
                    columnDataListResult.getData());
        } else {
            // 如果本次请求中包含从表数据，则一同插入。
            ResponseResult<Map<OnlineDatasourceRelation, List<List<ColumnData>>>> slaveDataListResult =
                    onlineOperationHelper.buildSlaveDataList(datasourceId, slaveData);
            if (!slaveDataListResult.isSuccess()) {
                return ResponseResult.errorFrom(slaveDataListResult);
            }
            flowOnlineOperationService.saveNewAndTakeTask(
                    processInstanceId,
                    taskId,
                    flowTaskComment,
                    taskVariableData,
                    masterTable,
                    columnDataListResult.getData(),
                    slaveDataListResult.getData());
        }
        return ResponseResult.success();
    }

    private ResponseResult<OnlineTable> verifyAndGetOnlineTable(
            Long datasourceId, Long relationId, String businessKey, String dataId) {
        ResponseResult<OnlineDatasource> datasourceResult =
                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineTable masterTable = datasourceResult.getData().getMasterTable();
        OnlineTable table = masterTable;
        ResponseResult<OnlineDatasourceRelation> relationResult = null;
        if (relationId != null) {
            relationResult = onlineOperationHelper.verifyAndGetRelation(datasourceId, relationId);
            if (!relationResult.isSuccess()) {
                return ResponseResult.errorFrom(relationResult);
            }
            table = relationResult.getData().getSlaveTable();
        }
        if (StrUtil.hasBlank(businessKey, dataId)) {
            return ResponseResult.success(table);
        }
        String errorMessage;
        // 如果relationId为null，这里就是主表数据。
        if (relationId == null) {
            if (!StrUtil.equals(businessKey, dataId)) {
                errorMessage = "数据验证失败，参数主键Id与流程主表主键Id不匹配！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            return ResponseResult.success(table);
        }
        OnlineDatasourceRelation relation = relationResult.getData();
        OnlineTable slaveTable = relation.getSlaveTable();
        Map<String, Object> dataMap =
                onlineOperationService.getMasterData(slaveTable, null, null, dataId);
        if (dataMap == null) {
            errorMessage = "数据验证失败，从表主键Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineColumn slaveColumn = relation.getSlaveColumn();
        Object relationSlaveDataId = dataMap.get(slaveColumn.getColumnName());
        if (relationSlaveDataId == null) {
            errorMessage = "数据验证失败，当前关联的从表字段值为NULL！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineColumn masterColumn = masterTable.getColumnMap().get(relation.getMasterColumnId());
        if (masterColumn.getPrimaryKey()) {
            if (!StrUtil.equals(relationSlaveDataId.toString(), businessKey)) {
                errorMessage = "数据验证失败，当前从表主键Id关联的主表Id当前流程的BusinessKey不一致！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        Map<String, Object> masterDataMap =
                onlineOperationService.getMasterData(masterTable, null, null, businessKey);
        if (masterDataMap == null) {
            errorMessage = "数据验证失败，主表主键Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Object relationMasterDataId = masterDataMap.get(masterColumn.getColumnName());
        if (relationMasterDataId == null) {
            errorMessage = "数据验证失败，当前关联的主表字段值为NULL！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!StrUtil.equals(relationMasterDataId.toString(), relationSlaveDataId.toString())) {
            errorMessage = "数据验证失败，当前关联的主表字段值和从表字段值不一致！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(table);
    }

    private ResponseResult<String> verifyUploadOrDownload(
            String processDefinitionKey, String processInstanceId, String taskId, Long datasourceId) {
        if (!StrUtil.isAllBlank(processInstanceId, taskId)) {
            ResponseResult<Void> verifyResult =
                    flowOperationHelper.verifyUploadOrDownloadPermission(processInstanceId, taskId);
            if (!verifyResult.isSuccess()) {
                return ResponseResult.errorFrom(ResponseResult.errorFrom(verifyResult));
            }
        }
        String errorMessage;
        FlowEntry flowEntry = flowEntryService.getFlowEntryByProcessDefinitionKey(processDefinitionKey);
        if (flowEntry == null) {
            errorMessage = "数据验证失败，指定流程Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        String businessKey = null;
        if (processInstanceId != null) {
            HistoricProcessInstance instance = flowApiService.getHistoricProcessInstance(processInstanceId);
            if (!StrUtil.equals(flowEntry.getProcessDefinitionKey(), instance.getProcessDefinitionKey())) {
                errorMessage = "数据验证失败，指定流程实例并不属于当前流程！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            businessKey = instance.getBusinessKey();
        }
        List<OnlinePageDatasource> datasourceList =
                onlinePageService.getOnlinePageDatasourceListByPageId(flowEntry.getPageId());
        Optional<Long> r = datasourceList.stream()
                .map(OnlinePageDatasource::getDatasourceId).filter(c -> c.equals(datasourceId)).findFirst();
        if (!r.isPresent()) {
            errorMessage = "数据验证失败，当前数据源Id并不属于当前流程！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(businessKey);
    }

    private ResponseResult<Void> makeWorkOrderTaskInfo(List<FlowWorkOrderVo> flowWorkOrderVoList) {
        if (CollUtil.isEmpty(flowWorkOrderVoList)) {
            return ResponseResult.success();
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
        Set<String> businessKeySet =
                flowWorkOrderVoList.stream().map(FlowWorkOrderVo::getBusinessKey).collect(Collectors.toSet());
        Long tableId = flowWorkOrderVoList.get(0).getOnlineTableId();
        OnlineTable masterTable = onlineTableService.getOnlineTableFromCache(tableId);
        Set<?> convertedBusinessKeySet =
                onlineOperationHelper.convertToTypeValue(masterTable.getPrimaryKeyColumn(), businessKeySet);
        List<OnlineFilterDto> filterList = new LinkedList<>();
        OnlineFilterDto filterDto = new OnlineFilterDto();
        filterDto.setTableName(masterTable.getTableName());
        filterDto.setColumnName(masterTable.getPrimaryKeyColumn().getColumnName());
        filterDto.setFilterType(FieldFilterType.IN_LIST_FILTER);
        filterDto.setColumnValue(convertedBusinessKeySet);
        TaskInfoVo taskInfoVo = JSON.parseObject(flowWorkOrderVoList.get(0).getInitTaskInfo(), TaskInfoVo.class);
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfoVo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasource.getDatasourceId(), RelationType.ONE_TO_ONE);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        List<Map<String, Object>> dataList = onlineOperationService.getMasterDataList(
                masterTable, relationListResult.getData(), null, filterList, null);
        Map<Object, Map<String, Object>> dataMap = dataList.stream()
                .collect(Collectors.toMap(c -> c.get(masterTable.getPrimaryKeyColumn().getColumnName()), c -> c));
        for (FlowWorkOrderVo flowWorkOrderVo : flowWorkOrderVoList) {
            Object dataId = onlineOperationHelper.convertToTypeValue(
                    masterTable.getPrimaryKeyColumn(), flowWorkOrderVo.getBusinessKey());
            Map<String, Object> data = dataMap.get(dataId);
            if (data != null) {
                flowWorkOrderVo.setMasterData(data);
            }
        }
        List<String> unfinishedProcessInstanceIds = flowWorkOrderVoList.stream()
                .filter(c -> !c.getFlowStatus().equals(FlowTaskStatus.FINISHED))
                .map(FlowWorkOrderVo::getProcessInstanceId)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(unfinishedProcessInstanceIds)) {
            return ResponseResult.success();
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
        return ResponseResult.success();
    }
}
