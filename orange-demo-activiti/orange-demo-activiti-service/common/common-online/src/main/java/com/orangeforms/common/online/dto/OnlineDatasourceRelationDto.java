package com.orangeforms.common.online.dto;

import com.orangeforms.common.core.validator.AddGroup;
import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.model.constant.RelationType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 在线表单的数据源关联Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineDatasourceRelationDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long relationId;

    /**
     * 关联名称。
     */
    @NotBlank(message = "数据验证失败，关联名称不能为空！")
    private String relationName;

    /**
     * 变量名。
     */
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    private String variableName;

    /**
     * 主数据源Id。
     */
    @NotNull(message = "数据验证失败，主数据源Id不能为空！")
    private Long datasourceId;

    /**
     * 关联类型。
     */
    @NotNull(message = "数据验证失败，关联类型不能为空！")
    @ConstDictRef(constDictClass = RelationType.class, message = "数据验证失败，关联类型为无效值！")
    private Integer relationType;

    /**
     * 主表关联字段Id。
     */
    @NotNull(message = "数据验证失败，主表关联字段Id不能为空！")
    private Long masterColumnId;

    /**
     * 从表Id。
     */
    @NotNull(message = "数据验证失败，从表Id不能为空！", groups = {UpdateGroup.class})
    private Long slaveTableId;

    /**
     * 从表名。
     */
    @NotBlank(message = "数据验证失败，从表名不能为空！", groups = {AddGroup.class})
    private String slaveTableName;

    /**
     * 从表关联字段Id。
     */
    @NotNull(message = "数据验证失败，从表关联字段Id不能为空！", groups = {UpdateGroup.class})
    private Long slaveColumnId;

    /**
     * 从表字段名。
     */
    @NotBlank(message = "数据验证失败，从表字段名不能为空！", groups = {AddGroup.class})
    private String slaveColumnName;

    /**
     * 是否级联删除标记。
     */
    @NotNull(message = "数据验证失败，是否级联删除标记不能为空！")
    private Boolean cascadeDelete;

    /**
     * 是否左连接标记。
     */
    @NotNull(message = "数据验证失败，是否左连接标记不能为空！")
    private Boolean leftJoin;
}
