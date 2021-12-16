package com.orangeforms.common.flow.model;

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

    /**
     * 保存岗位相关的数据。
     */
    @TableField(value = "dept_post_list_json")
    private String deptPostListJson;

    /**
     * 逗号分隔的角色Id。
     */
    @TableField(value = "role_ids")
    private String roleIds;

    /**
     * 逗号分隔的部门Id。
     */
    @TableField(value = "dept_ids")
    private String deptIds;

    /**
     * 逗号分隔候选用户名。
     */
    @TableField(value = "candidate_usernames")
    private String candidateUsernames;
}
