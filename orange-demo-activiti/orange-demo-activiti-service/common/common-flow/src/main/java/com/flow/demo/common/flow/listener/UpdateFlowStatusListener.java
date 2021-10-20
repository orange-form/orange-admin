package com.flow.demo.common.flow.listener;

import cn.hutool.core.util.StrUtil;
import com.flow.demo.common.core.util.ApplicationContextHolder;
import com.flow.demo.common.flow.service.FlowWorkOrderService;
import com.flow.demo.common.flow.constant.FlowTaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 流程实例监听器，在流程实例结束的时候更新流程工单表的审批状态字段。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class UpdateFlowStatusListener implements ExecutionListener {

    private final FlowWorkOrderService flowWorkOrderService =
            ApplicationContextHolder.getBean(FlowWorkOrderService.class);

    @Override
    public void notify(DelegateExecution execution) {
        if (!StrUtil.equals("end", execution.getEventName())) {
            return;
        }
        String processInstanceId = execution.getProcessInstanceId();
        flowWorkOrderService.updateFlowStatusByProcessInstanceId(processInstanceId, FlowTaskStatus.FINISHED);
    }
}
