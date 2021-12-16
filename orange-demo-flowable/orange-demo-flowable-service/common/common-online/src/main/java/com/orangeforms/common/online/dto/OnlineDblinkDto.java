package com.orangeforms.common.online.dto;

import com.orangeforms.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 在线表单数据表所在数据库链接Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineDblinkDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long dblinkId;

    /**
     * 链接中文名称。
     */
    @NotBlank(message = "数据验证失败，链接中文名称不能为空！")
    private String dblinkName;

    /**
     * 链接英文名称。
     */
    @NotBlank(message = "数据验证失败，链接英文名称不能为空！")
    private String variableName;

    /**
     * 链接描述。
     */
    private String dblinkDesc;

    /**
     * 数据源配置常量。
     */
    @NotNull(message = "数据验证失败，数据源配置常量不能为空！")
    private Integer dblinkConfigConstant;
}
