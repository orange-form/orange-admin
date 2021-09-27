package com.flow.demo.common.flow.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程的Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowEntryVo {

    /**
     * 主键Id。
     */
    private Long entryId;

    /**
     * 流程名称。
     */
    private String processDefinitionName;

    /**
     * 流程标识Key。
     */
    private String processDefinitionKey;

    /**
     * 流程分类。
     */
    private Long categoryId;

    /**
     * 工作流部署的发布主版本Id。
     */
    private Long mainEntryPublishId;

    /**
     * 最新发布时间。
     */
    private Date lastestPublishTime;

    /**
     * 流程状态。
     */
    private Integer status;

    /**
     * 流程定义的xml。
     */
    private String bpmnXml;

    /**
     * 绑定表单类型。
     */
    private Integer bindFormType;

    /**
     * 在线表单的页面Id。
     */
    private Long pageId;

    /**
     * 在线表单Id。
     */
    private Long defaultFormId;

    /**
     * 在线表单的缺省路由名称。
     */
    private String defaultRouterName;

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
     * categoryId 的一对一关联数据对象，数据对应类型为FlowCategoryVo。
     */
    private Map<String, Object> flowCategory;

    /**
     * mainEntryPublishId 的一对一关联数据对象，数据对应类型为FlowEntryPublishVo。
     */
    private Map<String, Object> mainFlowEntryPublish;

    /**
     * 关联的在线表单列表。
     */
    private List<Map<String, Object>> formList;
}
