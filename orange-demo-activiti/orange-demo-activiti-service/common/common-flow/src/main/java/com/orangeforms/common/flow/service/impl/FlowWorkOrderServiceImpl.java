package com.orangeforms.common.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.core.constant.GlobalDeletedFlag;
import com.orangeforms.common.core.object.MyRelationParam;
import com.orangeforms.common.core.object.TokenData;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.flow.constant.FlowTaskStatus;
import com.orangeforms.common.flow.dao.FlowWorkOrderMapper;
import com.orangeforms.common.flow.model.FlowWorkOrder;
import com.orangeforms.common.flow.service.FlowWorkOrderService;
import com.orangeforms.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 工作流工单表数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowWorkOrderService")
public class FlowWorkOrderServiceImpl extends BaseService<FlowWorkOrder, Long> implements FlowWorkOrderService {

    @Autowired
    private FlowWorkOrderMapper flowWorkOrderMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowWorkOrder> mapper() {
        return flowWorkOrderMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param instance      流程实例对象。
     * @param dataId        流程实例的BusinessKey。
     * @param onlineTableId 在线数据表的主键Id。
     * @param tableName     面向静态表单所使用的表名。
     * @return 新增的工作流工单对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder saveNew(ProcessInstance instance, Object dataId, Long onlineTableId, String tableName) {
        TokenData tokenData = TokenData.takeFromRequest();
        Date now = new Date();
        FlowWorkOrder flowWorkOrder = new FlowWorkOrder();
        flowWorkOrder.setWorkOrderId(idGenerator.nextLongId());
        flowWorkOrder.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        flowWorkOrder.setProcessDefinitionName(instance.getProcessDefinitionName());
        flowWorkOrder.setProcessDefinitionId(instance.getProcessDefinitionId());
        flowWorkOrder.setProcessInstanceId(instance.getId());
        flowWorkOrder.setBusinessKey(dataId.toString());
        flowWorkOrder.setOnlineTableId(onlineTableId);
        flowWorkOrder.setTableName(tableName);
        flowWorkOrder.setFlowStatus(FlowTaskStatus.SUBMITTED);
        flowWorkOrder.setSubmitUsername(tokenData.getLoginName());
        flowWorkOrder.setDeptId(tokenData.getDeptId());
        flowWorkOrder.setCreateUserId(tokenData.getUserId());
        flowWorkOrder.setUpdateUserId(tokenData.getUserId());
        flowWorkOrder.setCreateTime(now);
        flowWorkOrder.setUpdateTime(now);
        flowWorkOrder.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        flowWorkOrderMapper.insert(flowWorkOrder);
        return flowWorkOrder;
    }

    /**
     * 删除指定数据。
     *
     * @param workOrderId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long workOrderId) {
        return flowWorkOrderMapper.deleteById(workOrderId) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByProcessInstanceId(String processInstanceId) {
        FlowWorkOrder filter = new FlowWorkOrder();
        filter.setProcessInstanceId(processInstanceId);
        super.removeBy(filter);
    }

    @Override
    public List<FlowWorkOrder> getFlowWorkOrderList(FlowWorkOrder filter, String orderBy) {
        return flowWorkOrderMapper.getFlowWorkOrderList(filter, orderBy);
    }

    @Override
    public List<FlowWorkOrder> getFlowWorkOrderListWithRelation(FlowWorkOrder filter, String orderBy) {
        List<FlowWorkOrder> resultList = flowWorkOrderMapper.getFlowWorkOrderList(filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    @Override
    public FlowWorkOrder getFlowWorkOrderByProcessInstanceId(String processInstanceId) {
        FlowWorkOrder filter = new FlowWorkOrder();
        filter.setProcessInstanceId(processInstanceId);
        return flowWorkOrderMapper.selectOne(new QueryWrapper<>(filter));
    }

    @Override
    public boolean existByBusinessKey(String tableName, Object businessKey, boolean unfinished) {
        LambdaQueryWrapper<FlowWorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowWorkOrder::getBusinessKey, businessKey.toString());
        queryWrapper.eq(FlowWorkOrder::getTableName, tableName);
        if (unfinished) {
            queryWrapper.notIn(FlowWorkOrder::getFlowStatus,
                    FlowTaskStatus.FINISHED, FlowTaskStatus.CANCELLED, FlowTaskStatus.STOPPED);
        }
        return flowWorkOrderMapper.selectCount(queryWrapper) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFlowStatusByProcessInstanceId(String processInstanceId, int flowStatus) {
        FlowWorkOrder flowWorkOrder = new FlowWorkOrder();
        flowWorkOrder.setFlowStatus(flowStatus);
        if (FlowTaskStatus.FINISHED != flowStatus) {
            flowWorkOrder.setUpdateTime(new Date());
            flowWorkOrder.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        }
        LambdaQueryWrapper<FlowWorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowWorkOrder::getProcessInstanceId, processInstanceId);
        flowWorkOrderMapper.update(flowWorkOrder, queryWrapper);
    }
}
