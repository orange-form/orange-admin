package com.orangeforms.common.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.orangeforms.common.flow.object.FlowTaskPostCandidateGroup;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.object.CallResult;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import com.orangeforms.common.flow.util.BaseFlowIdentityExtHelper;
import com.orangeforms.common.flow.util.FlowCustomExtFactory;
import com.orangeforms.common.flow.constant.FlowConstant;
import com.orangeforms.common.flow.dao.*;
import com.orangeforms.common.flow.model.*;
import com.orangeforms.common.flow.service.*;
import com.orangeforms.common.flow.model.constant.FlowEntryStatus;
import com.orangeforms.common.flow.model.constant.FlowVariableType;
import com.orangeforms.common.flow.listener.FlowFinishedListener;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FlowEntry数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowEntryService")
public class FlowEntryServiceImpl extends BaseService<FlowEntry, Long> implements FlowEntryService {

    @Autowired
    private FlowEntryMapper flowEntryMapper;
    @Autowired
    private FlowEntryPublishMapper flowEntryPublishMapper;
    @Autowired
    private FlowEntryPublishVariableMapper flowEntryPublishVariableMapper;
    @Autowired
    private FlowEntryVariableService flowEntryVariableService;
    @Autowired
    private FlowCategoryService flowCategoryService;
    @Autowired
    private FlowTaskExtService flowTaskExtService;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowEntry> mapper() {
        return flowEntryMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param flowEntry 新增工作流对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowEntry saveNew(FlowEntry flowEntry) {
        flowEntry.setEntryId(idGenerator.nextLongId());
        flowEntry.setStatus(FlowEntryStatus.UNPUBLISHED);
        TokenData tokenData = TokenData.takeFromRequest();
        flowEntry.setUpdateUserId(tokenData.getUserId());
        flowEntry.setCreateUserId(tokenData.getUserId());
        Date now = new Date();
        flowEntry.setUpdateTime(now);
        flowEntry.setCreateTime(now);
        flowEntryMapper.insert(flowEntry);
        this.insertBuiltinEntryVariables(flowEntry.getEntryId());
        return flowEntry;
    }

    /**
     * 发布指定流程。
     *
     * @param flowEntry       待发布的流程对象。
     * @param initTaskInfo    第一个非开始节点任务的附加信息。
     * @param flowTaskExtList 所有用户任务的自定义扩展数据列表。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void publish(FlowEntry flowEntry, String initTaskInfo, List<FlowTaskExt> flowTaskExtList) throws XMLStreamException {
        FlowCategory flowCategory = flowCategoryService.getById(flowEntry.getCategoryId());
        InputStream xmlStream = new ByteArrayInputStream(
                flowEntry.getBpmnXml().getBytes(StandardCharsets.UTF_8));
        @Cleanup XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlStream);
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = converter.convertToBpmnModel(reader);
        flowApiService.addProcessInstanceEndListener(bpmnModel, FlowFinishedListener.class);
        Collection<FlowElement> elementList = bpmnModel.getMainProcess().getFlowElements();
        Map<String, FlowElement> elementMap =
                elementList.stream().filter(e -> e instanceof UserTask).collect(Collectors.toMap(FlowElement::getId, c -> c));
        if (CollUtil.isNotEmpty(flowTaskExtList)) {
            BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
            for (FlowTaskExt t : flowTaskExtList) {
                UserTask userTask = (UserTask) elementMap.get(t.getTaskId());
                // 如果流程图中包含部门领导审批和上级部门领导审批的选项，就需要注册 FlowCustomExtFactory 工厂中的
                // BaseFlowIdentityExtHelper 对象，该注册操作需要业务模块中实现。
                if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER)) {
                    userTask.setCandidateGroups(
                            CollUtil.newArrayList("${" + FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR + "}"));
                    Assert.notNull(flowIdentityExtHelper);
                    flowApiService.addTaskCreateListener(userTask, flowIdentityExtHelper.getUpDeptPostLeaderListener());
                } else if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER)) {
                    userTask.setCandidateGroups(
                            CollUtil.newArrayList("${" + FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR + "}"));
                    Assert.notNull(flowIdentityExtHelper);
                    flowApiService.addTaskCreateListener(userTask, flowIdentityExtHelper.getDeptPostLeaderListener());
                } else if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_POST)) {
                    Assert.notNull(t.getDeptPostListJson());
                    List<FlowTaskPostCandidateGroup> groupDataList =
                            JSONArray.parseArray(t.getDeptPostListJson(), FlowTaskPostCandidateGroup.class);
                    List<String> candidateGroupList =
                            FlowTaskPostCandidateGroup.buildCandidateGroupList(groupDataList);
                    userTask.setCandidateGroups(candidateGroupList);
                }
            }
        }
        Deployment deploy = repositoryService.createDeployment()
                .addBpmnModel(flowEntry.getProcessDefinitionKey() + ".bpmn", bpmnModel)
                .name(flowEntry.getProcessDefinitionName())
                .key(flowEntry.getProcessDefinitionKey())
                .category(flowCategory.getCode())
                .deploy();
        ProcessDefinition processDefinition = flowApiService.getProcessDefinitionByDeployId(deploy.getId());
        FlowEntryPublish flowEntryPublish = new FlowEntryPublish();
        flowEntryPublish.setEntryPublishId(idGenerator.nextLongId());
        flowEntryPublish.setEntryId(flowEntry.getEntryId());
        flowEntryPublish.setProcessDefinitionId(processDefinition.getId());
        flowEntryPublish.setDeployId(processDefinition.getDeploymentId());
        flowEntryPublish.setPublishVersion(processDefinition.getVersion());
        flowEntryPublish.setActiveStatus(true);
        flowEntryPublish.setMainVersion(flowEntry.getStatus().equals(FlowEntryStatus.UNPUBLISHED));
        flowEntryPublish.setCreateUserId(TokenData.takeFromRequest().getUserId());
        flowEntryPublish.setPublishTime(new Date());
        flowEntryPublish.setInitTaskInfo(initTaskInfo);
        flowEntryPublishMapper.insert(flowEntryPublish);
        FlowEntry updatedFlowEntry = new FlowEntry();
        updatedFlowEntry.setEntryId(flowEntry.getEntryId());
        updatedFlowEntry.setStatus(FlowEntryStatus.PUBLISHED);
        updatedFlowEntry.setLastestPublishTime(new Date());
        // 对于从未发布过的工作，第一次发布的时候会将本地发布置位主版本。
        if (flowEntry.getStatus().equals(FlowEntryStatus.UNPUBLISHED)) {
            updatedFlowEntry.setMainEntryPublishId(flowEntryPublish.getEntryPublishId());
        }
        flowEntryMapper.updateById(updatedFlowEntry);
        FlowEntryVariable flowEntryVariableFilter = new FlowEntryVariable();
        flowEntryVariableFilter.setEntryId(flowEntry.getEntryId());
        List<FlowEntryVariable> flowEntryVariableList =
                flowEntryVariableService.getFlowEntryVariableList(flowEntryVariableFilter, null);
        if (CollUtil.isNotEmpty(flowTaskExtList)) {
            flowTaskExtList.forEach(t -> t.setProcessDefinitionId(processDefinition.getId()));
            flowTaskExtService.saveBatch(flowTaskExtList);
        }
        this.insertEntryPublishVariables(flowEntryVariableList, flowEntryPublish.getEntryPublishId());
    }

    /**
     * 更新数据对象。
     *
     * @param flowEntry         更新的对象。
     * @param originalFlowEntry 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(FlowEntry flowEntry, FlowEntry originalFlowEntry) {
        flowEntry.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        flowEntry.setCreateUserId(originalFlowEntry.getCreateUserId());
        flowEntry.setUpdateTime(new Date());
        flowEntry.setCreateTime(originalFlowEntry.getCreateTime());
        flowEntry.setPageId(originalFlowEntry.getPageId());
        return flowEntryMapper.updateById(flowEntry) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param entryId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long entryId) {
        if (flowEntryMapper.deleteById(entryId) != 1) {
            return false;
        }
        flowEntryVariableService.removeByEntryId(entryId);
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getFlowEntryListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<FlowEntry> getFlowEntryList(FlowEntry filter, String orderBy) {
        return flowEntryMapper.getFlowEntryList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getFlowEntryList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<FlowEntry> getFlowEntryListWithRelation(FlowEntry filter, String orderBy) {
        List<FlowEntry> resultList = flowEntryMapper.getFlowEntryList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        Set<Long> mainEntryPublishIdSet = resultList.stream().filter(e -> e.getMainEntryPublishId() != null)
                .map(FlowEntry::getMainEntryPublishId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(mainEntryPublishIdSet)) {
            List<FlowEntryPublish> mainEntryPublishList =
                    flowEntryPublishMapper.selectBatchIds(mainEntryPublishIdSet);
            MyModelUtil.makeOneToOneRelation(FlowEntry.class, resultList, FlowEntry::getMainEntryPublishId,
                    mainEntryPublishList, FlowEntryPublish::getEntryPublishId, "mainFlowEntryPublish");
        }
        return resultList;
    }

    @Override
    public FlowEntry getFlowEntryByProcessDefinitionKey(String processDefinitionKey) {
        FlowEntry filter = new FlowEntry();
        filter.setProcessDefinitionKey(processDefinitionKey);
        return flowEntryMapper.selectOne(new LambdaQueryWrapper<>(filter));
    }

    /**
     * 根据流程Id获取流程发布列表数据。
     *
     * @param entryId 流程Id。
     * @return 流程关联的发布列表数据。
     */
    @Override
    public List<FlowEntryPublish> getFlowEntryPublishList(Long entryId) {
        FlowEntryPublish filter = new FlowEntryPublish();
        filter.setEntryId(entryId);
        LambdaQueryWrapper<FlowEntryPublish> queryWrapper = new LambdaQueryWrapper<>(filter);
        queryWrapper.orderByDesc(FlowEntryPublish::getEntryPublishId);
        return flowEntryPublishMapper.selectList(queryWrapper);
    }

    /**
     * 根据流程引擎中的流程定义Id集合，查询流程发布对象。
     *
     * @param processDefinitionIdSet 流程引擎中的流程定义Id集合。
     * @return 查询结果。
     */
    @Override
    public List<FlowEntryPublish> getFlowEntryPublishList(Set<String> processDefinitionIdSet) {
        LambdaQueryWrapper<FlowEntryPublish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FlowEntryPublish::getProcessDefinitionId, processDefinitionIdSet);
        return flowEntryPublishMapper.selectList(queryWrapper);
    }

    /**
     * 获取指定工作流发布版本对象。同时同步修改工作流对象中冗余状态字段。
     *
     * @param entryPublishId 工作流发布对象Id。
     * @return 查询后的对象。
     */
    @Override
    public FlowEntryPublish getFlowEntryPublishById(Long entryPublishId) {
        return flowEntryPublishMapper.selectById(entryPublishId);
    }

    /**
     * 获取指定流程定义Id对应的流程发布对象。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 流程发布对象。
     */
    @Override
    public FlowEntryPublish getFlowEntryPublishByDefinitionId(String processDefinitionId) {
        LambdaQueryWrapper<FlowEntryPublish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowEntryPublish::getProcessDefinitionId, processDefinitionId);
        return flowEntryPublishMapper.selectOne(queryWrapper);
    }

    /**
     * 为指定工作流切换发布的主版本。
     *
     * @param flowEntry               工作流对象。
     * @param newMainFlowEntryPublish 工作流新的发布主版本对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFlowEntryMainVersion(FlowEntry flowEntry, FlowEntryPublish newMainFlowEntryPublish) {
        FlowEntryPublish oldMainFlowEntryPublish =
                flowEntryPublishMapper.selectById(flowEntry.getMainEntryPublishId());
        oldMainFlowEntryPublish.setMainVersion(false);
        flowEntryPublishMapper.updateById(oldMainFlowEntryPublish);
        newMainFlowEntryPublish.setMainVersion(true);
        flowEntryPublishMapper.updateById(newMainFlowEntryPublish);
        FlowEntry updatedEntry = new FlowEntry();
        updatedEntry.setEntryId(flowEntry.getEntryId());
        updatedEntry.setMainEntryPublishId(newMainFlowEntryPublish.getEntryPublishId());
        flowEntryMapper.updateById(updatedEntry);
    }

    /**
     * 挂起指定的工作流发布对象。同时同步修改工作流对象中冗余状态字段。
     *
     * @param flowEntryPublish 待挂起的工作流发布对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void suspendFlowEntryPublish(FlowEntryPublish flowEntryPublish) {
        FlowEntryPublish updatedEntryPublish = new FlowEntryPublish();
        updatedEntryPublish.setEntryPublishId(flowEntryPublish.getEntryPublishId());
        updatedEntryPublish.setActiveStatus(false);
        flowEntryPublishMapper.updateById(updatedEntryPublish);
        flowApiService.suspendProcessDefinition(flowEntryPublish.getProcessDefinitionId());
    }

    /**
     * 激活指定的工作流发布对象。
     *
     * @param flowEntryPublish 待恢复的工作流发布对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void activateFlowEntryPublish(FlowEntryPublish flowEntryPublish) {
        FlowEntryPublish updatedEntryPublish = new FlowEntryPublish();
        updatedEntryPublish.setEntryPublishId(flowEntryPublish.getEntryPublishId());
        updatedEntryPublish.setActiveStatus(true);
        flowEntryPublishMapper.updateById(updatedEntryPublish);
        flowApiService.activateProcessDefinition(flowEntryPublish.getProcessDefinitionId());
    }

    /**
     * 主表的关联数据验证。
     *
     * @param flowEntry         最新数据对象。
     * @param originalFlowEntry 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    @Override
    public CallResult verifyRelatedData(FlowEntry flowEntry, FlowEntry originalFlowEntry) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        if (this.needToVerify(flowEntry, originalFlowEntry, FlowEntry::getCategoryId)
                && !flowCategoryService.existId(flowEntry.getCategoryId())) {
            return CallResult.error(String.format(errorMessageFormat, "流程类别Id"));
        }
        return CallResult.ok();
    }

    private void insertBuiltinEntryVariables(Long entryId) {
        Date now = new Date();
        FlowEntryVariable operationTypeVariable = new FlowEntryVariable();
        operationTypeVariable.setVariableId(idGenerator.nextLongId());
        operationTypeVariable.setEntryId(entryId);
        operationTypeVariable.setVariableName(FlowConstant.OPERATION_TYPE_VAR);
        operationTypeVariable.setShowName("审批类型");
        operationTypeVariable.setVariableType(FlowVariableType.TASK);
        operationTypeVariable.setBuiltin(true);
        operationTypeVariable.setCreateTime(now);
        flowEntryVariableService.saveNew(operationTypeVariable);
        FlowEntryVariable startUserNameVariable = new FlowEntryVariable();
        startUserNameVariable.setVariableId(idGenerator.nextLongId());
        startUserNameVariable.setEntryId(entryId);
        startUserNameVariable.setVariableName("startUserName");
        startUserNameVariable.setShowName("流程启动用户");
        startUserNameVariable.setVariableType(FlowVariableType.INSTANCE);
        startUserNameVariable.setBuiltin(true);
        startUserNameVariable.setCreateTime(now);
        flowEntryVariableService.saveNew(startUserNameVariable);
    }

    private void insertEntryPublishVariables(List<FlowEntryVariable> entryVariableList, Long entryPublishId) {
        if (CollUtil.isEmpty(entryVariableList)) {
            return;
        }
        List<FlowEntryPublishVariable> entryPublishVariableList =
                MyModelUtil.copyCollectionTo(entryVariableList, FlowEntryPublishVariable.class);
        for (FlowEntryPublishVariable variable : entryPublishVariableList) {
            variable.setVariableId(idGenerator.nextLongId());
            variable.setEntryPublishId(entryPublishId);
        }
        flowEntryPublishVariableMapper.insertList(entryPublishVariableList);
    }
}
