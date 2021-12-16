package com.orangeforms.common.flow.vo;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 工作流工单VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowWorkOrderVo {

    /**
     * 主键Id。
     */
    private Long workOrderId;

    /**
     * 流程定义标识。
     */
    private String processDefinitionKey;

    /**
     * 流程名称。
     */
    private String processDefinitionName;

    /**
     * 流程引擎的定义Id。
     */
    private String processDefinitionId;

    /**
     * 流程实例Id。
     */
    private String processInstanceId;

    /**
     * 在线表单的主表Id。
     */
    private Long onlineTableId;

    /**
     * 业务主键值。
     */
    private String businessKey;

    /**
     * 流程状态。参考FlowTaskStatus常量值对象。
     */
    private Integer flowStatus;

    /**
     * 提交用户登录名称。
     */
    private String submitUsername;

    /**
     * 提交用户所在部门Id。
     */
    private Long deptId;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 更新者Id。
     */
    private Long updateUserId;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 创建者Id。
     */
    private Long createUserId;

    /**
     * flowStatus 常量字典关联数据。
     */
    private Map<String, Object> flowStatusDictMap;

    /**
     * FlowEntryPublish对象中的同名字段。
     */
    private String initTaskInfo;

    /**
     * 当前实例的运行时任务列表。
     * 正常情况下只有一个，在并行网关下可能存在多个。
     */
    private JSONArray runtimeTaskInfoList;

    /**
     * 业务主表数据。
     */
    private Map<String, Object> masterData;
}
