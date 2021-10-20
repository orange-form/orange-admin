package com.flow.demo.common.flow.command;

import com.flow.demo.common.core.exception.MyRuntimeException;
import com.flow.demo.common.flow.constant.FlowConstant;
import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.io.Serializable;

/**
 * 多实例加签命令对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class AddMultiInstanceExecutionCmd implements Command<Void>, Serializable {

    protected final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
    protected final String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";
    protected String collectionElementIndexVariable = "loopCounter";
    private String startTaskId;
    private String taskId;
    private String assignee;

    public AddMultiInstanceExecutionCmd(String startTaskId, String taskId, String assignee) {
        this.startTaskId = startTaskId;
        this.taskId = taskId;
        this.assignee = assignee;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        //根据任务id获取任务实例
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        //根据执行实例ID获取当前执行实例
        ExecutionEntity multiExecutionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        // 获取流程执行实例（即当前执行实例的父实例）
        ExecutionEntity parentExecutionEntity = multiExecutionEntity.getParent();
        //判断当前执行实例的节点是否是多实例节点
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(multiExecutionEntity.getProcessDefinitionId());
        Activity miActivityElement = (Activity) bpmnModel.getFlowElement(multiExecutionEntity.getCurrentActivityId());
        MultiInstanceLoopCharacteristics loopCharacteristics = miActivityElement.getLoopCharacteristics();
        if (loopCharacteristics == null) {
            throw new MyRuntimeException("此节点不是多实例节点！");
        }
        //判断是否是并行多实例
        if (loopCharacteristics.isSequential()) {
            throw new MyRuntimeException("此节点为串行多实例节点！");
        }
        //创建新的子实例
        ExecutionEntity childExecution = executionEntityManager.createChildExecution(parentExecutionEntity);
        //获取并为新的执行实例设置当前活动节点
        UserTask currentFlowElement = (UserTask) multiExecutionEntity.getCurrentFlowElement();
        //设置处理人
        childExecution.setCurrentFlowElement(currentFlowElement);
        childExecution.setVariableLocal("assignee", assignee);
        childExecution.setVariableLocal(FlowConstant.MULTI_SIGN_START_TASK_VAR, startTaskId);
        //获取设置变量
        Integer nrOfInstances = (Integer) parentExecutionEntity.getVariableLocal(FlowConstant.NUMBER_OF_INSTANCES_VAR);
        Integer nrOfActiveInstances = (Integer) parentExecutionEntity.getVariableLocal(NUMBER_OF_ACTIVE_INSTANCES);
        parentExecutionEntity.setVariableLocal(FlowConstant.NUMBER_OF_INSTANCES_VAR, nrOfInstances + 1);
        parentExecutionEntity.setVariableLocal(NUMBER_OF_ACTIVE_INSTANCES, nrOfActiveInstances + 1);
        //通知活动开始
        HistoryManager historyManager = commandContext.getHistoryManager();
        historyManager.recordActivityStart(childExecution);
        //获取处理行为类
        ParallelMultiInstanceBehavior prallelMultiInstanceBehavior =
                (ParallelMultiInstanceBehavior) miActivityElement.getBehavior();
        AbstractBpmnActivityBehavior innerActivityBehavior = prallelMultiInstanceBehavior.getInnerActivityBehavior();
        //执行
        innerActivityBehavior.execute(childExecution);
        return null;
    }
}

