package com.orangeforms.common.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.flow.constant.FlowConstant;
import com.orangeforms.common.flow.model.constant.FlowMessageType;
import com.orangeforms.common.flow.dao.FlowMessageCandidateIdentityMapper;
import com.orangeforms.common.flow.dao.FlowMessageMapper;
import com.orangeforms.common.flow.model.FlowMessage;
import com.orangeforms.common.flow.model.FlowMessageCandidateIdentity;
import com.orangeforms.common.flow.model.FlowTaskExt;
import com.orangeforms.common.flow.model.FlowWorkOrder;
import com.orangeforms.common.flow.object.FlowTaskPostCandidateGroup;
import com.orangeforms.common.flow.service.FlowApiService;
import com.orangeforms.common.flow.service.FlowMessageService;
import com.orangeforms.common.flow.service.FlowTaskExtService;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 工作流消息数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowMessageService")
public class FlowMessageServiceImpl extends BaseService<FlowMessage, Long> implements FlowMessageService {

    @Autowired
    private FlowMessageMapper flowMessageMapper;
    @Autowired
    private FlowMessageCandidateIdentityMapper flowMessageCandidateIdentityMapper;
    @Autowired
    private FlowTaskExtService flowTaskExtService;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowMessage> mapper() {
        return flowMessageMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowMessage saveNew(FlowMessage flowMessage) {
        flowMessage.setMessageId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        flowMessage.setCreateUserId(tokenData.getUserId());
        flowMessage.setCreateUsername(tokenData.getShowName());
        flowMessage.setCreateTime(new Date());
        flowMessage.setUpdateUserId(tokenData.getUserId());
        flowMessage.setUpdateTime(flowMessage.getCreateTime());
        flowMessageMapper.insert(flowMessage);
        return flowMessage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewRemindMessage(FlowWorkOrder flowWorkOrder) {
        List<Task> taskList =
                flowApiService.getProcessInstanceActiveTaskList(flowWorkOrder.getProcessInstanceId());
        for (Task task : taskList) {
            FlowMessage filter = new FlowMessage();
            filter.setTaskId(task.getId());
            List<FlowMessage> messageList = flowMessageMapper.selectList(new QueryWrapper<>(filter));
            // 同一个任务只能催办一次，多次催办则累加催办次数。
            if (CollUtil.isNotEmpty(messageList)) {
                for (FlowMessage flowMessage : messageList) {
                    flowMessage.setRemindCount(flowMessage.getRemindCount() + 1);
                    flowMessageMapper.updateById(flowMessage);
                }
                continue;
            }
            FlowMessage flowMessage = new FlowMessage();
            flowMessage.setMessageType(FlowMessageType.REMIND_TYPE);
            flowMessage.setRemindCount(1);
            flowMessage.setWorkOrderId(flowWorkOrder.getWorkOrderId());
            flowMessage.setProcessDefinitionId(flowWorkOrder.getProcessDefinitionId());
            flowMessage.setProcessDefinitionKey(flowWorkOrder.getProcessDefinitionKey());
            flowMessage.setProcessDefinitionName(flowWorkOrder.getProcessDefinitionName());
            flowMessage.setProcessInstanceId(flowWorkOrder.getProcessInstanceId());
            flowMessage.setProcessInstanceInitiator(flowWorkOrder.getSubmitUsername());
            flowMessage.setTaskId(task.getId());
            flowMessage.setTaskDefinitionKey(task.getTaskDefinitionKey());
            flowMessage.setTaskName(task.getName());
            flowMessage.setTaskStartTime(task.getCreateTime());
            flowMessage.setTaskAssignee(task.getAssignee());
            flowMessage.setTaskFinished(false);
            this.saveNew(flowMessage);
            FlowTaskExt flowTaskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(
                    flowWorkOrder.getProcessDefinitionId(), task.getTaskDefinitionKey());
            if (flowTaskExt != null) {
                this.saveMessageCandidateIdentityWithMessage(
                        flowWorkOrder.getProcessInstanceId(), flowTaskExt, flowMessage.getMessageId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFinishedStatusByTaskId(String taskId) {
        FlowMessage flowMessage = new FlowMessage();
        flowMessage.setTaskFinished(true);
        LambdaQueryWrapper<FlowMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowMessage::getTaskId, taskId);
        flowMessageMapper.update(flowMessage, queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFinishedStatusByProcessInstanceId(String processInstanceId) {
        FlowMessage flowMessage = new FlowMessage();
        flowMessage.setTaskFinished(true);
        LambdaQueryWrapper<FlowMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowMessage::getProcessInstanceId, processInstanceId);
        flowMessageMapper.update(flowMessage, queryWrapper);
    }

    @Override
    public List<FlowMessage> getRemindingMessageListByUser() {
        TokenData tokenData = TokenData.takeFromRequest();
        Set<String> groupIdSet = new HashSet<>(1);
        groupIdSet.add(tokenData.getLoginName());
        this.parseAndAddIdArray(groupIdSet, tokenData.getRoleIds());
        this.parseAndAddIdArray(groupIdSet, tokenData.getDeptPostIds());
        this.parseAndAddIdArray(groupIdSet, tokenData.getPostIds());
        if (tokenData.getDeptId() != null) {
            groupIdSet.add(tokenData.getDeptId().toString());
        }
        return flowMessageMapper.getRemindingMessageListByUser(tokenData.getLoginName(), groupIdSet);
    }

    private void parseAndAddIdArray(Set<String> groupIdSet, String idArray) {
        if (StrUtil.isNotBlank(idArray)) {
            if (groupIdSet == null) {
                groupIdSet = new HashSet<>();
            }
            groupIdSet.addAll(StrUtil.split(idArray, ','));
        }
    }

    private void saveMessageCandidateIdentityWithMessage(
            String processInstanceId, FlowTaskExt flowTaskExt, Long messageId) {
        this.saveMessageCandidateIdentityList(
                messageId, "username", flowTaskExt.getCandidateUsernames());
        this.saveMessageCandidateIdentityList(
                messageId, "role", flowTaskExt.getRoleIds());
        this.saveMessageCandidateIdentityList(
                messageId, "dept", flowTaskExt.getDeptIds());
        if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER)) {
            Object v = flowApiService.getProcessInstanceVariable(
                    processInstanceId, FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR);
            if (v != null) {
                this.saveMessageCandidateIdentity(
                        messageId, FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR, v.toString());
            }
        } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER)) {
            Object v = flowApiService.getProcessInstanceVariable(
                    processInstanceId, FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR);
            if (v != null) {
                this.saveMessageCandidateIdentity(
                        messageId, FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR, v.toString());
            }
        } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_POST)) {
            Assert.notBlank(flowTaskExt.getDeptPostListJson());
            List<FlowTaskPostCandidateGroup> groupDataList =
                    JSONArray.parseArray(flowTaskExt.getDeptPostListJson(), FlowTaskPostCandidateGroup.class);
            for (FlowTaskPostCandidateGroup groupData : groupDataList) {
                FlowMessageCandidateIdentity candidateIdentity = new FlowMessageCandidateIdentity();
                candidateIdentity.setId(idGenerator.nextLongId());
                candidateIdentity.setMessageId(messageId);
                candidateIdentity.setCandidateType(groupData.getType());
                switch (groupData.getType()) {
                    case FlowConstant.GROUP_TYPE_ALL_DEPT_POST_VAR:
                        candidateIdentity.setCandidateId(groupData.getPostId());
                        flowMessageCandidateIdentityMapper.insert(candidateIdentity);
                        break;
                    case FlowConstant.GROUP_TYPE_DEPT_POST_VAR:
                        candidateIdentity.setCandidateId(groupData.getDeptPostId());
                        flowMessageCandidateIdentityMapper.insert(candidateIdentity);
                        break;
                    case FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR:
                        Object v = flowApiService.getProcessInstanceVariable(
                                processInstanceId, FlowConstant.SELF_DEPT_POST_PREFIX + groupData.getPostId());
                        if (v != null) {
                            candidateIdentity.setCandidateId(v.toString());
                            flowMessageCandidateIdentityMapper.insert(candidateIdentity);
                        }
                        break;
                    case FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR:
                        Object v2 = flowApiService.getProcessInstanceVariable(
                                processInstanceId, FlowConstant.UP_DEPT_POST_PREFIX + groupData.getPostId());
                        if (v2 != null) {
                            candidateIdentity.setCandidateId(v2.toString());
                            flowMessageCandidateIdentityMapper.insert(candidateIdentity);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void saveMessageCandidateIdentity(Long messageId, String candidateType, String candidateId) {
        FlowMessageCandidateIdentity candidateIdentity = new FlowMessageCandidateIdentity();
        candidateIdentity.setId(idGenerator.nextLongId());
        candidateIdentity.setMessageId(messageId);
        candidateIdentity.setCandidateType(FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR);
        candidateIdentity.setCandidateId(candidateId);
        flowMessageCandidateIdentityMapper.insert(candidateIdentity);
    }

    private void saveMessageCandidateIdentityList(Long messageId, String candidateType, String identityIds) {
        if (StrUtil.isNotBlank(identityIds)) {
            for (String identityId : StrUtil.split(identityIds, ',')) {
                FlowMessageCandidateIdentity candidateIdentity = new FlowMessageCandidateIdentity();
                candidateIdentity.setId(idGenerator.nextLongId());
                candidateIdentity.setMessageId(messageId);
                candidateIdentity.setCandidateType(candidateType);
                candidateIdentity.setCandidateId(identityId);
                flowMessageCandidateIdentityMapper.insert(candidateIdentity);
            }
        }
    }
}
