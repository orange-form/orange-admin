package com.orangeforms.common.online.dto;

import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.model.constant.FieldFilterType;
import com.orangeforms.common.online.model.constant.FieldKind;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 在线表单数据表字段Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineColumnDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long columnId;

    /**
     * 字段名。
     */
    @NotBlank(message = "数据验证失败，字段名不能为空！")
    private String columnName;

    /**
     * 数据表Id。
     */
    @NotNull(message = "数据验证失败，数据表Id不能为空！")
    private Long tableId;

    /**
     * 数据表中的字段类型。
     */
    @NotBlank(message = "数据验证失败，数据表中的字段类型不能为空！")
    private String columnType;

    /**
     * 数据表中的完整字段类型(包括了精度和刻度)。
     */
    @NotBlank(message = "数据验证失败，数据表中的完整字段类型(包括了精度和刻度)不能为空！")
    private String fullColumnType;

    /**
     * 是否为主键。
     */
    @NotNull(message = "数据验证失败，是否为主键不能为空！")
    private Boolean primaryKey;

    /**
     * 是否是自增主键(0: 不是 1: 是)。
     */
    @NotNull(message = "数据验证失败，是否是自增主键(0: 不是 1: 是)不能为空！")
    private Boolean autoIncrement;

    /**
     * 是否可以为空 (0: 不可以为空 1: 可以为空)。
     */
    @NotNull(message = "数据验证失败，是否可以为空 (0: 不可以为空 1: 可以为空)不能为空！")
    private Boolean nullable;

    /**
     * 缺省值。
     */
    private String columnDefault;

    /**
     * 字段在数据表中的显示位置。
     */
    @NotNull(message = "数据验证失败，字段在数据表中的显示位置不能为空！")
    private Integer columnShowOrder;

    /**
     * 数据表中的字段注释。
     */
    private String columnComment;

    /**
     * 对象映射字段名称。
     */
    @NotBlank(message = "数据验证失败，对象映射字段名称不能为空！")
    private String objectFieldName;

    /**
     * 对象映射字段类型。
     */
    @NotBlank(message = "数据验证失败，对象映射字段类型不能为空！")
    private String objectFieldType;

    /**
     * 过滤类型字段。
     */
    @NotNull(message = "数据验证失败，过滤类型字段不能为空！", groups = {UpdateGroup.class})
    @ConstDictRef(constDictClass = FieldFilterType.class, message = "数据验证失败，过滤类型字段为无效值！")
    private Integer filterType;

    /**
     * 是否是主键的父Id。
     */
    @NotNull(message = "数据验证失败，是否是主键的父Id不能为空！")
    private Boolean parentKey;

    /**
     * 是否部门过滤字段。
     */
    @NotNull(message = "数据验证失败，是否部门过滤字段标记不能为空！")
    private Boolean deptFilter;

    /**
     * 是否用户过滤字段。
     */
    @NotNull(message = "数据验证失败，是否用户过滤字段标记不能为空！")
    private Boolean userFilter;

    /**
     * 字段类别。
     */
    @ConstDictRef(constDictClass = FieldKind.class, message = "数据验证失败，字段类别为无效值！")
    private Integer fieldKind;

    /**
     * 包含的文件文件数量，0表示无限制。
     */
    private Integer maxFileCount;

    /**
     * 字典Id。
     */
    private Long dictId;
}
