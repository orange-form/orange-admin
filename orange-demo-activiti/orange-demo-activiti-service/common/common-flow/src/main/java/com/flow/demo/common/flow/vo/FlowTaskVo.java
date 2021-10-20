package com.flow.demo.common.flow.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流程任务Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowTaskVo {

    /**
     * 流程任务Id。
     */
    private String taskId;

    /**
     * 流程任务名称。
     */
    private String taskName;

    /**
     * 流程任务标识。
     */
    private String taskKey;

    /**
     * 任务的表单信息。
     */
    private String taskFormKey;

    /**
     * 流程Id。
     */
    private Long entryId;

    /**
     * 流程定义Id。
     */
    private String processDefinitionId;

    /**
     * 流程定义名称。
     */
    private String processDefinitionName;

    /**
     * 流程定义标识。
     */
    private String processDefinitionKey;

    /**
     * 流程定义版本。
     */
    private Integer processDefinitionVersion;

    /**
     * 流程实例Id。
     */
    private String processInstanceId;

    /**
     * 流程实例发起人。
     */
    private String processInstanceInitiator;

    /**
     * 流程实例创建时间。
     */
    private Date processInstanceStartTime;
}
