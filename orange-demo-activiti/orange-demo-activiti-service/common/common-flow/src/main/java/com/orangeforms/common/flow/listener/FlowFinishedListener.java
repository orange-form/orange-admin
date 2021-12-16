package com.orangeforms.common.flow.listener;

import cn.hutool.core.util.StrUtil;
import com.orangeforms.common.core.util.ApplicationContextHolder;
import com.orangeforms.common.flow.model.FlowWorkOrder;
import com.orangeforms.common.flow.service.FlowWorkOrderService;
import com.orangeforms.common.flow.constant.FlowTaskStatus;
import com.orangeforms.common.flow.util.FlowCustomExtFactory;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 流程实例监听器，在流程实例结束的时候，需要完成一些自定义的业务行为。如：
 * 1. 更新流程工单表的审批状态字段。
 * 2. 业务数据同步。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class FlowFinishedListener implements ExecutionListener {

    private final FlowWorkOrderService flowWorkOrderService =
            ApplicationContextHolder.getBean(FlowWorkOrderService.class);
    private final FlowCustomExtFactory flowCustomExtFactory =
            ApplicationContextHolder.getBean(FlowCustomExtFactory.class);

    @Override
    public void notify(DelegateExecution execution) {
        if (!StrUtil.equals("end", execution.getEventName())) {
            return;
        }
        String processInstanceId = execution.getProcessInstanceId();
        flowWorkOrderService.updateFlowStatusByProcessInstanceId(processInstanceId, FlowTaskStatus.FINISHED);
        String businessKey = execution.getProcessInstanceBusinessKey();
        FlowWorkOrder workOrder = flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(processInstanceId);
        flowCustomExtFactory.getDataSyncExtHelper()
                .triggerSync(workOrder.getProcessDefinitionKey(), processInstanceId, businessKey);
    }
}
