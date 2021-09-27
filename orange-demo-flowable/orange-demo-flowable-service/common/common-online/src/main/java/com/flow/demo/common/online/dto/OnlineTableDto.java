package com.flow.demo.common.online.dto;

import com.flow.demo.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 在线表单的数据表Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineTableDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long tableId;

    /**
     * 表名称。
     */
    @NotBlank(message = "数据验证失败，表名称不能为空！")
    private String tableName;

    /**
     * 实体名称。
     */
    @NotBlank(message = "数据验证失败，实体名称不能为空！")
    private String modelName;

    /**
     * 数据库链接Id。
     */
    @NotNull(message = "数据验证失败，数据库链接Id不能为空！")
    private Long dblinkId;
}
