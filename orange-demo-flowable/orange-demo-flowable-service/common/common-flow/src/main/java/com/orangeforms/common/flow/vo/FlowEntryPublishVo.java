package com.orangeforms.common.flow.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流程发布信息的Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowEntryPublishVo {

    /**
     * 主键Id。
     */
    private Long entryPublishId;

    /**
     * 发布版本。
     */
    private Integer publishVersion;

    /**
     * 流程引擎中的流程定义Id。
     */
    private String processDefinitionId;

    /**
     * 激活状态。
     */
    private Boolean activeStatus;

    /**
     * 是否为主版本。
     */
    private Boolean mainVersion;

    /**
     * 创建者Id。
     */
    private Long createUserId;

    /**
     * 发布时间。
     */
    private Date publishTime;
}
