package com.orangeforms.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 流程发布数据的实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_flow_entry_publish")
public class FlowEntryPublish {

    /**
     * 主键Id。
     */
    @TableId(value = "entry_publish_id")
    private Long entryPublishId;

    /**
     * 流程Id。
     */
    @TableField(value = "entry_id")
    private Long entryId;

    /**
     * 流程引擎的部署Id。
     */
    @TableField(value = "deploy_id")
    private String deployId;

    /**
     * 流程引擎中的流程定义Id。
     */
    @TableField(value = "process_definition_id")
    private String processDefinitionId;

    /**
     * 发布版本。
     */
    @TableField(value = "publish_version")
    private Integer publishVersion;

    /**
     * 激活状态。
     */
    @TableField(value = "active_status")
    private Boolean activeStatus;

    /**
     * 是否为主版本。
     */
    @TableField(value = "main_version")
    private Boolean mainVersion;

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 发布时间。
     */
    @TableField(value = "publish_time")
    private Date publishTime;

    /**
     * 第一个非开始节点任务的附加信息。
     */
    @TableField(value = "init_task_info")
    private String initTaskInfo;
}
