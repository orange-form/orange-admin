package com.orangeforms.common.flow.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.jimmyshi.beanquery.BeanQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.flow.object.FlowTaskMultiSignAssign;
import com.orangeforms.common.flow.constant.FlowTaskType;
import com.orangeforms.common.flow.dto.*;
import com.orangeforms.common.flow.model.*;
import com.orangeforms.common.flow.model.constant.FlowEntryStatus;
import com.orangeforms.common.flow.service.*;
import com.orangeforms.common.flow.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * 工作流操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-flow.urlPrefix}/flowEntry")
public class FlowEntryController {

    @Autowired
    private FlowEntryService flowEntryService;
    @Autowired
    private FlowCategoryService flowCategoryService;
    @Autowired
    private FlowEntryVariableService flowEntryVariableService;
    @Autowired
    private FlowApiService flowApiService;

    /**
     * 新增工作流对象数据。
     *
     * @param flowEntryDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody FlowEntryDto flowEntryDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(flowEntryDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        FlowEntry flowEntry = MyModelUtil.copyTo(flowEntryDto, FlowEntry.class);
        if (flowEntryService.existOne("processDefinitionKey", flowEntry.getProcessDefinitionKey())) {
            errorMessage = "数据验证失败，该流程定义标识已存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = flowEntryService.verifyRelatedData(flowEntry, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        flowEntry = flowEntryService.saveNew(flowEntry);
        return ResponseResult.success(flowEntry.getEntryId());
    }

    /**
     * 更新工作流对象数据。
     *
     * @param flowEntryDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody FlowEntryDto flowEntryDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(flowEntryDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        FlowEntry flowEntry = MyModelUtil.copyTo(flowEntryDto, FlowEntry.class);
        FlowEntry originalFlowEntry = flowEntryService.getById(flowEntry.getEntryId());
        if (originalFlowEntry == null) {
            errorMessage = "数据验证失败，当前流程并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (ObjectUtil.notEqual(flowEntry.getProcessDefinitionKey(), originalFlowEntry.getProcessDefinitionKey())) {
            if (originalFlowEntry.getStatus().equals(FlowEntryStatus.PUBLISHED)) {
                errorMessage = "数据验证失败，当前流程为发布状态，流程标识不能修改！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (flowEntryService.existOne("processDefinitionKey", flowEntry.getProcessDefinitionKey())) {
                errorMessage = "数据验证失败，该流程定义标识已存在！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        if (ObjectUtil.notEqual(flowEntry.getCategoryId(), originalFlowEntry.getCategoryId())) {
            if (originalFlowEntry.getStatus().equals(FlowEntryStatus.PUBLISHED)) {
                errorMessage = "数据验证失败，当前流程为发布状态，流程分类不能修改！";
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
            }
        }
        // 验证关联Id的数据合法性
        CallResult callResult = flowEntryService.verifyRelatedData(flowEntry, originalFlowEntry);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!flowEntryService.update(flowEntry, originalFlowEntry)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除工作流对象数据。
     *
     * @param entryId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long entryId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(entryId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        FlowEntry originalFlowEntry = flowEntryService.getById(entryId);
        if (originalFlowEntry == null) {
            errorMessage = "数据验证失败，当前流程并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (originalFlowEntry.getStatus().equals(FlowEntryStatus.PUBLISHED)) {
            errorMessage = "数据验证失败，当前流程为发布状态，不能删除！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!flowEntryService.remove(entryId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 发布工作流。
     *
     * @param entryId 流程主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/publish")
    public ResponseResult<Void> publish(@MyRequestBody(required = true) Long entryId) throws XMLStreamException {
        String errorMessage;
        FlowEntry flowEntry = flowEntryService.getById(entryId);
        if (flowEntry == null) {
            errorMessage = "数据验证失败，该流程并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (StrUtil.isBlank(flowEntry.getBpmnXml())) {
            errorMessage = "数据验证失败，该流程没有流程图不能被发布！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        ResponseResult<TaskInfoVo> taskInfoResult = this.verifyAndGetInitialTaskInfo(flowEntry);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        List<FlowTaskExt> flowTaskExtList = this.buildTaskExtList(flowEntry);
        String taskInfo = taskInfoResult.getData() == null ? null : JSON.toJSONString(taskInfoResult.getData());
        flowEntryService.publish(flowEntry, taskInfo, flowTaskExtList);
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的工作流列表。
     *
     * @param flowEntryDtoFilter 过滤对象。
     * @param orderParam         排序参数。
     * @param pageParam          分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<FlowEntryVo>> list(
            @MyRequestBody FlowEntryDto flowEntryDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        FlowEntry flowEntryFilter = MyModelUtil.copyTo(flowEntryDtoFilter, FlowEntry.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, FlowEntry.class);
        List<FlowEntry> flowEntryList = flowEntryService.getFlowEntryListWithRelation(flowEntryFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(flowEntryList, FlowEntry.INSTANCE));
    }

    /**
     * 查看指定工作流对象详情。
     *
     * @param entryId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<FlowEntryVo> view(@RequestParam Long entryId) {
        if (MyCommonUtil.existBlankArgument(entryId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        FlowEntry flowEntry = flowEntryService.getByIdWithRelation(entryId, MyRelationParam.full());
        if (flowEntry == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        FlowEntryVo flowEntryVo = FlowEntry.INSTANCE.fromModel(flowEntry);
        return ResponseResult.success(flowEntryVo);
    }

    /**
     * 列出指定流程的发布版本列表。
     *
     * @param entryId 流程主键Id。
     * @return 应答结果对象，包含流程发布列表数据。
     */
    @GetMapping("/listFlowEntryPublish")
    public ResponseResult<List<FlowEntryPublishVo>> listFlowEntryPublish(@RequestParam Long entryId) {
        List<FlowEntryPublish> flowEntryPublishList = flowEntryService.getFlowEntryPublishList(entryId);
        return ResponseResult.success(MyModelUtil.copyCollectionTo(flowEntryPublishList, FlowEntryPublishVo.class));
    }

    /**
     * 以字典形式返回全部FlowEntry数据集合。字典的键值为[entryId, procDefinitionName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(FlowEntry filter) {
        List<FlowEntry> resultList = flowEntryService.getListByFilter(filter);
        return ResponseResult.success(BeanQuery.select(
                "entryId as id", "processDefinitionName as name").executeFrom(resultList));
    }

    /**
     * 白名单接口，根据流程Id，获取流程引擎需要的流程标识和流程名称。
     *
     * @param entryId 流程Id。
     * @return 流程的部分数据。
     */
    @GetMapping("/viewDict")
    public ResponseResult<Map<String, Object>> viewDict(@RequestParam Long entryId) {
        FlowEntry flowEntry = flowEntryService.getById(entryId);
        if (flowEntry == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("processDefinitionKey", flowEntry.getProcessDefinitionKey());
        resultMap.put("processDefinitionName", flowEntry.getProcessDefinitionName());
        return ResponseResult.success(resultMap);
    }

    /**
     * 切换指定工作的发布主版本。
     *
     * @param entryId           工作流主键Id。
     * @param newEntryPublishId 新的工作流发布主版本对象的主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/updateMainVersion")
    public ResponseResult<Void> updateMainVersion(
            @MyRequestBody(required = true) Long entryId,
            @MyRequestBody(required = true) Long newEntryPublishId) {
        String errorMessage;
        FlowEntryPublish flowEntryPublish = flowEntryService.getFlowEntryPublishById(newEntryPublishId);
        if (flowEntryPublish == null) {
            errorMessage = "数据验证失败，当前流程发布版本并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (ObjectUtil.notEqual(entryId, flowEntryPublish.getEntryId())) {
            errorMessage = "数据验证失败，当前工作流并不包含该工作流发布版本数据，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (flowEntryPublish.getMainVersion()) {
            errorMessage = "数据验证失败，该版本已经为当前工作流的发布主版本，不能重复设置！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        flowEntryService.updateFlowEntryMainVersion(flowEntryService.getById(entryId), flowEntryPublish);
        return ResponseResult.success();
    }

    /**
     * 挂起工作流的指定发布版本。
     *
     * @param entryPublishId 工作发布Id。
     * @return 应答结果对象。
     */
    @PostMapping("/suspendFlowEntryPublish")
    public ResponseResult<Void> suspendFlowEntryPublish(@MyRequestBody(required = true) Long entryPublishId) {
        String errorMessage;
        FlowEntryPublish flowEntryPublish = flowEntryService.getFlowEntryPublishById(entryPublishId);
        if (flowEntryPublish == null) {
            errorMessage = "数据验证失败，当前流程发布版本并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!flowEntryPublish.getActiveStatus()) {
            errorMessage = "数据验证失败，当前流程发布版本已处于挂起状态！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        flowEntryService.suspendFlowEntryPublish(flowEntryPublish);
        return ResponseResult.success();
    }

    /**
     * 激活工作流的指定发布版本。
     *
     * @param entryPublishId 工作发布Id。
     * @return 应答结果对象。
     */
    @PostMapping("/activateFlowEntryPublish")
    public ResponseResult<Void> activateFlowEntryPublish(@MyRequestBody(required = true) Long entryPublishId) {
        String errorMessage;
        FlowEntryPublish flowEntryPublish = flowEntryService.getFlowEntryPublishById(entryPublishId);
        if (flowEntryPublish == null) {
            errorMessage = "数据验证失败，当前流程发布版本并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (flowEntryPublish.getActiveStatus()) {
            errorMessage = "数据验证失败，当前流程发布版本已处于激活状态！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        flowEntryService.activateFlowEntryPublish(flowEntryPublish);
        return ResponseResult.success();
    }

    private FlowTaskExt buildTaskExt(UserTask userTask) {
        FlowTaskExt flowTaskExt = new FlowTaskExt();
        flowTaskExt.setTaskId(userTask.getId());
        String formKey = userTask.getFormKey();
        if (StrUtil.isNotBlank(formKey)) {
            TaskInfoVo taskInfoVo = JSON.parseObject(formKey, TaskInfoVo.class);
            flowTaskExt.setGroupType(taskInfoVo.getGroupType());
        }
        Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionMap)) {
            List<JSONObject> operationList = this.buildOperationListExtensionElement(extensionMap);
            if (CollUtil.isNotEmpty(operationList)) {
                flowTaskExt.setOperationListJson(JSON.toJSONString(operationList));
            }
            List<JSONObject> variableList = this.buildVariableListExtensionElement(extensionMap);
            if (CollUtil.isNotEmpty(variableList)) {
                flowTaskExt.setVariableListJson(JSON.toJSONString(variableList));
            }
            JSONObject assigneeListObject = this.buildAssigneeListExtensionElement(extensionMap);
            if (assigneeListObject != null) {
                flowTaskExt.setAssigneeListJson(JSON.toJSONString(assigneeListObject));
            }
            List<JSONObject> deptPostList = this.buildDeptPostListExtensionElement(extensionMap);
            if (deptPostList != null) {
                flowTaskExt.setDeptPostListJson(JSON.toJSONString(deptPostList));
            }
            JSONObject candidateGroupObject = this.buildUserCandidateGroupsExtensionElement(extensionMap);
            if (candidateGroupObject != null) {
                String type = candidateGroupObject.getString("type");
                String value = candidateGroupObject.getString("value");
                switch (type) {
                    case "DEPT":
                        flowTaskExt.setDeptIds(value);
                        break;
                    case "ROLE":
                        flowTaskExt.setRoleIds(value);
                        break;
                    case "USERS":
                        flowTaskExt.setCandidateUsernames(value);
                        break;
                    default:
                        break;
                }
            }
        }
        return flowTaskExt;
    }

    private List<FlowTaskExt> buildTaskExtList(FlowEntry flowEntry) throws XMLStreamException {
        List<FlowTaskExt> flowTaskExtList = new LinkedList<>();
        BpmnModel bpmnModel = flowApiService.convertToBpmnModel(flowEntry.getBpmnXml());
        List<Process> processList = bpmnModel.getProcesses();
        for (Process process : processList) {
            for (FlowElement element : process.getFlowElements()) {
                if (element instanceof UserTask) {
                    FlowTaskExt flowTaskExt = this.buildTaskExt((UserTask) element);
                    flowTaskExtList.add(flowTaskExt);
                }
            }
        }
        return flowTaskExtList;
    }

    private ResponseResult<TaskInfoVo> verifyAndGetInitialTaskInfo(FlowEntry flowEntry) throws XMLStreamException {
        String errorMessage;
        BpmnModel bpmnModel = flowApiService.convertToBpmnModel(flowEntry.getBpmnXml());
        Process process = bpmnModel.getMainProcess();
        if (process == null) {
            errorMessage = "数据验证失败，当前流程标识 [" + flowEntry.getProcessDefinitionKey() + "] 关联的流程模型并不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Collection<FlowElement> elementList = process.getFlowElements();
        FlowElement startEvent = null;
        FlowElement firstTask = null;
        // 这里我们只定位流程模型中的第二个节点。
        for (FlowElement flowElement : elementList) {
            if (flowElement instanceof StartEvent) {
                startEvent = flowElement;
                break;
            }
        }
        if (startEvent == null) {
            errorMessage = "数据验证失败，当前流程图没有包含 [开始事件] 节点，请修改流程图！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        for (FlowElement flowElement : elementList) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                if (sequenceFlow.getSourceFlowElement().equals(startEvent)) {
                    firstTask = sequenceFlow.getTargetFlowElement();
                    break;
                }
            }
        }
        if (firstTask == null) {
            errorMessage = "数据验证失败，当前流程图没有包含 [开始事件] 节点没有任何连线，请修改流程图！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        TaskInfoVo taskInfoVo;
        if (firstTask instanceof UserTask) {
            UserTask userTask = (UserTask) firstTask;
            String formKey = userTask.getFormKey();
            if (StrUtil.isNotBlank(formKey)) {
                taskInfoVo = JSON.parseObject(formKey, TaskInfoVo.class);
            } else {
                taskInfoVo = new TaskInfoVo();
            }
            taskInfoVo.setAssignee(userTask.getAssignee());
            taskInfoVo.setTaskKey(userTask.getId());
            taskInfoVo.setTaskType(FlowTaskType.USER_TYPE);
            Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
            if (MapUtil.isNotEmpty(extensionMap)) {
                taskInfoVo.setOperationList(this.buildOperationListExtensionElement(extensionMap));
                taskInfoVo.setVariableList(this.buildVariableListExtensionElement(extensionMap));
            }
        } else {
            taskInfoVo = new TaskInfoVo();
            taskInfoVo.setTaskType(FlowTaskType.OTHER_TYPE);
        }
        return ResponseResult.success(taskInfoVo);
    }

    private JSONObject buildUserCandidateGroupsExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        JSONObject jsonData = null;
        List<ExtensionElement> elementCandidateGroupsList = extensionMap.get("userCandidateGroups");
        if (CollUtil.isEmpty(elementCandidateGroupsList)) {
            return jsonData;
        }
        jsonData = new JSONObject();
        ExtensionElement ee = elementCandidateGroupsList.get(0);
        jsonData.put("type", ee.getAttributeValue(null, "type"));
        jsonData.put("value", ee.getAttributeValue(null, "value"));
        return jsonData;
    }

    private JSONObject buildAssigneeListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        JSONObject jsonData = null;
        List<ExtensionElement> elementAssigneeList = extensionMap.get("assigneeList");
        if (CollUtil.isEmpty(elementAssigneeList)) {
            return jsonData;
        }
        ExtensionElement ee = elementAssigneeList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return jsonData;
        }
        List<ExtensionElement> assigneeElements = childExtensionMap.get("assignee");
        if (CollUtil.isEmpty(assigneeElements)) {
            return jsonData;
        }
        JSONArray assigneeIdArray = new JSONArray();
        for (ExtensionElement e : assigneeElements) {
            assigneeIdArray.add(e.getAttributeValue(null, "id"));
        }
        jsonData = new JSONObject();
        String assigneeType = ee.getAttributeValue(null, "type");
        jsonData.put("assigneeType", assigneeType);
        jsonData.put("assigneeList", assigneeIdArray);
        return jsonData;
    }

    private List<JSONObject> buildOperationListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formOperationElements =
                this.getMyExtensionElementList(extensionMap, "operationList", "formOperation");
        if (CollUtil.isEmpty(formOperationElements)) {
            return null;
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : formOperationElements) {
            JSONObject operationJsonData = new JSONObject();
            operationJsonData.put("id", e.getAttributeValue(null, "id"));
            operationJsonData.put("label", e.getAttributeValue(null, "label"));
            operationJsonData.put("type", e.getAttributeValue(null, "type"));
            operationJsonData.put("showOrder", e.getAttributeValue(null, "showOrder"));
            String multiSignAssignee = e.getAttributeValue(null, "multiSignAssignee");
            if (StrUtil.isNotBlank(multiSignAssignee)) {
                operationJsonData.put("multiSignAssignee",
                        JSON.parseObject(multiSignAssignee, FlowTaskMultiSignAssign.class));
            }
            resultList.add(operationJsonData);
        }
        return resultList;
    }

    private List<JSONObject> buildVariableListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formVariableElements =
                this.getMyExtensionElementList(extensionMap, "variableList", "formVariable");
        if (CollUtil.isEmpty(formVariableElements)) {
            return null;
        }
        Set<Long> variableIdSet = new HashSet<>();
        for (ExtensionElement e : formVariableElements) {
            String id = e.getAttributeValue(null, "id");
            variableIdSet.add(Long.parseLong(id));
        }
        List<FlowEntryVariable> variableList = flowEntryVariableService.getInList(variableIdSet);
        List<JSONObject> resultList = new LinkedList<>();
        for (FlowEntryVariable variable : variableList) {
            resultList.add((JSONObject) JSON.toJSON(variable));
        }
        return resultList;
    }

    private List<JSONObject> buildDeptPostListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> deptPostElements =
                this.getMyExtensionElementList(extensionMap, "deptPostList", "deptPost");
        if (CollUtil.isEmpty(deptPostElements)) {
            return null;
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : deptPostElements) {
            JSONObject deptPostJsonData = new JSONObject();
            deptPostJsonData.put("id", e.getAttributeValue(null, "id"));
            deptPostJsonData.put("type", e.getAttributeValue(null, "type"));
            String postId = e.getAttributeValue(null, "postId");
            if (postId != null) {
                deptPostJsonData.put("postId", postId);
            }
            String deptPostId = e.getAttributeValue(null, "deptPostId");
            if (deptPostId != null) {
                deptPostJsonData.put("deptPostId", deptPostId);
            }
            resultList.add(deptPostJsonData);
        }
        return resultList;
    }

    private List<ExtensionElement> getMyExtensionElementList(
            Map<String, List<ExtensionElement>> extensionMap, String rootName, String childName) {
        List<ExtensionElement> elementList = extensionMap.get(rootName);
        if (CollUtil.isEmpty(elementList)) {
            return null;
        }
        ExtensionElement ee = elementList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return null;
        }
        List<ExtensionElement> childrenElements = childExtensionMap.get(childName);
        if (CollUtil.isEmpty(childrenElements)) {
            return null;
        }
        return childrenElements;
    }
}
