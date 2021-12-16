package com.orangeforms.common.flow.vo;

import lombok.Data;

import java.util.Date;

/**
 * FlowTaskCommentVO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowTaskCommentVo {

    /**
     * 主键Id。
     */
    private Long id;

    /**
     * 流程实例Id。
     */
    private String processInstanceId;

    /**
     * 任务Id。
     */
    private String taskId;

    /**
     * 任务标识。
     */
    private String taskKey;

    /**
     * 任务名称。
     */
    private String taskName;

    /**
     * 审批类型。
     */
    private String approvalType;

    /**
     * 批注内容。
     */
    private String comment;

    /**
     * 委托指定人，比如加签、转办等。
     */
    private String delegateAssginee;

    /**
     * 创建者Id。
     */
    private Long createUserId;

    /**
     * 创建者显示名。
     */
    private String createUsername;

    /**
     * 创建时间。
     */
    private Date createTime;
}
