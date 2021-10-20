package com.flow.demo.common.flow.dto;

import com.flow.demo.common.core.validator.ConstDictRef;
import com.flow.demo.common.core.validator.UpdateGroup;
import com.flow.demo.common.flow.model.constant.FlowBindFormType;
import com.flow.demo.common.flow.model.constant.FlowEntryStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流程的Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowEntryDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键不能为空！", groups = {UpdateGroup.class})
    private Long entryId;

    /**
     * 流程名称。
     */
    @NotBlank(message = "数据验证失败，流程名称不能为空！")
    private String processDefinitionName;

    /**
     * 流程标识Key。
     */
    @NotBlank(message = "数据验证失败，流程标识Key不能为空！")
    private String processDefinitionKey;

    /**
     * 流程分类。
     */
    @NotNull(message = "数据验证失败，流程分类不能为空！")
    private Long categoryId;

    /**
     * 流程状态。
     */
    @ConstDictRef(constDictClass = FlowEntryStatus.class, message = "数据验证失败，工作流状态为无效值！")
    private Integer status;

    /**
     * 流程定义的xml。
     */
    private String bpmnXml;

    /**
     * 绑定表单类型。
     */
    @ConstDictRef(constDictClass = FlowBindFormType.class, message = "数据验证失败，工作流绑定表单类型为无效值！")
    @NotNull(message = "数据验证失败，工作流绑定表单类型不能为空！")
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
}
