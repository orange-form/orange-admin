package com.flow.demo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 流程任务扩展实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_flow_task_ext")
public class FlowTaskExt {

    /**
     * 流程引擎的定义Id。
     */
    @TableField(value = "process_definition_id")
    private String processDefinitionId;

    /**
     * 流程引擎任务Id。
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 操作列表JSON。
     */
    @TableField(value = "operation_list_json")
    private String operationListJson;

    /**
     * 变量列表JSON。
     */
    @TableField(value = "variable_list_json")
    private String variableListJson;

    /**
     * 存储多实例的assigneeList的JSON。
     */
    @TableField(value = "assignee_list_json")
    private String assigneeListJson;

    /**
     * 分组类型。
     */
    @TableField(value = "group_type")
    private String groupType;
}
