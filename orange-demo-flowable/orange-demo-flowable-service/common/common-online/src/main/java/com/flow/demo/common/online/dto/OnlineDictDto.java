package com.flow.demo.common.online.dto;

import com.flow.demo.common.core.validator.ConstDictRef;
import com.flow.demo.common.core.validator.UpdateGroup;
import com.flow.demo.common.online.model.constant.DictType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 在线表单关联的字典Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlineDictDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long dictId;

    /**
     * 字典名称。
     */
    @NotBlank(message = "数据验证失败，字典名称不能为空！")
    private String dictName;

    /**
     * 字典类型。
     */
    @NotNull(message = "数据验证失败，字典类型不能为空！")
    @ConstDictRef(constDictClass = DictType.class, message = "数据验证失败，字典类型为无效值！")
    private Integer dictType;

    /**
     * 数据库链接Id。
     */
    private Long dblinkId;

    /**
     * 字典表名称。
     */
    private String tableName;

    /**
     * 字典表键字段名称。
     */
    private String keyColumnName;

    /**
     * 字典表父键字段名称。
     */
    private String parentKeyColumnName;

    /**
     * 字典值字段名称。
     */
    private String valueColumnName;

    /**
     * 逻辑删除字段。
     */
    private String deletedColumnName;

    /**
     * 用户过滤滤字段名称。
     */
    private String userFilterColumnName;

    /**
     * 部门过滤字段名称。
     */
    private String deptFilterColumnName;

    /**
     * 租户过滤字段名称。
     */
    private String tenantFilterColumnName;

    /**
     * 是否树形标记。
     */
    @NotNull(message = "数据验证失败，是否树形标记不能为空！")
    private Boolean treeFlag;

    /**
     * 获取字典数据的url。
     */
    private String dictListUrl;

    /**
     * 根据主键id批量获取字典数据的url。
     */
    private String dictIdsUrl;

    /**
     * 字典的JSON数据。
     */
    private String dictDataJson;
}
