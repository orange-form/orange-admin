package com.flow.demo.common.online.dto;

import com.flow.demo.common.core.validator.AddGroup;
import com.flow.demo.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 在线表单的数据源Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineDatasourceDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long datasourceId;

    /**
     * 数据源名称。
     */
    @NotBlank(message = "数据验证失败，数据源名称不能为空！")
    private String datasourceName;

    /**
     * 数据源变量名，会成为数据访问url的一部分。
     */
    @NotBlank(message = "数据验证失败，数据源变量名不能为空！")
    private String variableName;

    /**
     * 主表所在的数据库链接Id。
     */
    @NotNull(message = "数据验证失败，数据库链接Id不能为空！")
    private Long dblinkId;

    /**
     * 主表Id。
     */
    @NotNull(message = "数据验证失败，主表Id不能为空！", groups = {UpdateGroup.class})
    private Long masterTableId;

    /**
     * 主表表名。
     */
    @NotBlank(message = "数据验证失败，主表名不能为空！", groups = {AddGroup.class})
    private String masterTableName;
}
