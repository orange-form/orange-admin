package com.orangeforms.common.flow.dto;

import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.flow.model.constant.FlowVariableType;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 流程变量Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowEntryVariableDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long variableId;

    /**
     * 流程Id。
     */
    @NotNull(message = "数据验证失败，流程Id不能为空！")
    private Long entryId;

    /**
     * 变量名。
     */
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    private String variableName;

    /**
     * 显示名。
     */
    @NotBlank(message = "数据验证失败，显示名不能为空！")
    private String showName;

    /**
     * 流程变量类型。
     */
    @ConstDictRef(constDictClass = FlowVariableType.class, message = "数据验证失败，流程变量类型为无效值！")
    @NotNull(message = "数据验证失败，流程变量类型不能为空！")
    private Integer variableType;

    /**
     * 绑定数据源Id。
     */
    private Long bindDatasourceId;

    /**
     * 绑定数据源关联Id。
     */
    private Long bindRelationId;

    /**
     * 绑定字段Id。
     */
    private Long bindColumnId;

    /**
     * 是否内置。
     */
    @NotNull(message = "数据验证失败，是否内置不能为空！")
    private Boolean builtin;
}
