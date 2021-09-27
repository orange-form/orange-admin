package com.flow.demo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.common.core.annotation.RelationOneToOne;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.common.flow.vo.FlowEntryVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

/**
 * 流程的实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_flow_entry")
public class FlowEntry {

    /**
     * 主键。
     */
    @TableId(value = "entry_id")
    private Long entryId;

    /**
     * 流程名称。
     */
    @TableField(value = "process_definition_name")
    private String processDefinitionName;

    /**
     * 流程标识Key。
     */
    @TableField(value = "process_definition_key")
    private String processDefinitionKey;

    /**
     * 流程分类。
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 工作流部署的发布主版本Id。
     */
    @TableField(value = "main_entry_publish_id")
    private Long mainEntryPublishId;

    /**
     * 最新发布时间。
     */
    @TableField(value = "lastest_publish_time")
    private Date lastestPublishTime;

    /**
     * 流程状态。
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 流程定义的xml。
     */
    @TableField(value = "bpmn_xml")
    private String bpmnXml;

    /**
     * 绑定表单类型。
     */
    @TableField(value = "bind_form_type")
    private Integer bindFormType;

    /**
     * 在线表单的页面Id。
     */
    @TableField(value = "page_id")
    private Long pageId;

    /**
     * 在线表单Id。
     */
    @TableField(value = "default_form_id")
    private Long defaultFormId;

    /**
     * 静态表单的缺省路由名称。
     */
    @TableField(value = "default_router_name")
    private String defaultRouterName;

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

    @TableField(exist = false)
    private FlowEntryPublish mainFlowEntryPublish;

    @RelationOneToOne(
            masterIdField = "categoryId",
            slaveServiceName = "flowCategoryService",
            slaveModelClass = FlowCategory.class,
            slaveIdField = "categoryId")
    @TableField(exist = false)
    private FlowCategory flowCategory;

    @Mapper
    public interface FlowEntryModelMapper extends BaseModelMapper<FlowEntryVo, FlowEntry> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param flowEntryVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "mainFlowEntryPublish", expression = "java(mapToBean(flowEntryVo.getMainFlowEntryPublish(), com.flow.demo.common.flow.model.FlowEntryPublish.class))")
        @Mapping(target = "flowCategory", expression = "java(mapToBean(flowEntryVo.getFlowCategory(), com.flow.demo.common.flow.model.FlowCategory.class))")
        @Override
        FlowEntry toModel(FlowEntryVo flowEntryVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param flowEntry 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "mainFlowEntryPublish", expression = "java(beanToMap(flowEntry.getMainFlowEntryPublish(), false))")
        @Mapping(target = "flowCategory", expression = "java(beanToMap(flowEntry.getFlowCategory(), false))")
        @Override
        FlowEntryVo fromModel(FlowEntry flowEntry);
    }
    public static final FlowEntryModelMapper INSTANCE = Mappers.getMapper(FlowEntryModelMapper.class);
}
