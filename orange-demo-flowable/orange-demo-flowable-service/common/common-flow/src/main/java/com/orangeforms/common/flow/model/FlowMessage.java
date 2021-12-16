package com.orangeforms.common.flow.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.common.flow.vo.FlowMessageVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Date;

/**
 * 工作流通知消息实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_flow_message")
public class FlowMessage {

    /**
     * 主键Id。
     */
    @TableId(value = "message_id")
    private Long messageId;

    /**
     * 消息类型。
     */
    @TableField(value = "message_type")
    private Integer messageType;

    /**
     * 消息内容。
     */
    @TableField(value = "message_content")
    private String messageContent;

    /**
     * 催办次数。
     */
    @TableField(value = "remind_count")
    private Integer remindCount;

    /**
     * 工单Id。
     */
    @TableField(value = "work_order_id")
    private Long workOrderId;

    /**
     * 流程定义Id。
     */
    @TableField(value = "process_definition_id")
    private String processDefinitionId;

    /**
     * 流程定义标识。
     */
    @TableField(value = "process_definition_key")
    private String processDefinitionKey;

    /**
     * 流程名称。
     */
    @TableField(value = "process_definition_name")
    private String processDefinitionName;

    /**
     * 流程实例Id。
     */
    @TableField(value = "process_instance_id")
    private String processInstanceId;

    /**
     * 流程实例发起者。
     */
    @TableField(value = "process_instance_initiator")
    private String processInstanceInitiator;

    /**
     * 流程任务Id。
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 流程任务定义标识。
     */
    @TableField(value = "task_definition_key")
    private String taskDefinitionKey;

    /**
     * 流程任务名称。
     */
    @TableField(value = "task_name")
    private String taskName;

    /**
     * 创建时间。
     */
    @TableField(value = "task_start_time")
    private Date taskStartTime;

    /**
     * 任务指派人登录名。
     */
    @TableField(value = "task_assignee")
    private String taskAssignee;

    /**
     * 任务是否已完成。
     */
    @TableField(value = "task_finished")
    private Boolean taskFinished;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 更新者Id。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建者显示名。
     */
    @TableField(value = "create_username")
    private String createUsername;

    @Mapper
    public interface FlowMessageModelMapper extends BaseModelMapper<FlowMessageVo, FlowMessage> {
    }
    public static final FlowMessage.FlowMessageModelMapper INSTANCE = Mappers.getMapper(FlowMessage.FlowMessageModelMapper.class);
}
