package com.orangeforms.common.online.dto;

import com.orangeforms.common.core.constant.AggregationType;
import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.core.validator.UpdateGroup;

import com.orangeforms.common.online.model.constant.VirtualType;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 在线数据表虚拟字段Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineVirtualColumnDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long virtualColumnId;

    /**
     * 所在表Id。
     */
    private Long tableId;

    /**
     * 字段名称。
     */
    @NotBlank(message = "数据验证失败，字段名称不能为空！")
    private String objectFieldName;

    /**
     * 属性类型。
     */
    @NotBlank(message = "数据验证失败，属性类型不能为空！")
    private String objectFieldType;

    /**
     * 字段提示名。
     */
    @NotBlank(message = "数据验证失败，字段提示名不能为空！")
    private String columnPrompt;

    /**
     * 虚拟字段类型(0: 聚合)。
     */
    @ConstDictRef(constDictClass = VirtualType.class, message = "数据验证失败，虚拟字段类型为无效值！")
    @NotNull(message = "数据验证失败，虚拟字段类型(0: 聚合)不能为空！")
    private Integer virtualType;

    /**
     * 关联数据源Id。
     */
    @NotNull(message = "数据验证失败，关联数据源Id不能为空！")
    private Long datasourceId;

    /**
     * 关联Id。
     */
    private Long relationId;

    /**
     * 聚合字段所在关联表Id。
     */
    private Long aggregationTableId;

    /**
     * 关联表聚合字段Id。
     */
    private Long aggregationColumnId;

    /**
     * 聚合类型(0: sum 1: count 2: avg 3: min 4: max)。
     */
    @ConstDictRef(constDictClass = AggregationType.class, message = "数据验证失败，虚拟字段聚合计算类型为无效值！")
    private Integer aggregationType;

    /**
     * 存储过滤条件的json。
     */
    private String whereClauseJson;
}
