package com.orangeforms.webadmin.upms.dto;

import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.core.validator.ConstDictRef;
import com.orangeforms.common.datafilter.constant.DataPermRuleType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 数据权限Dto。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("数据权限Dto")
@Data
public class SysDataPermDto {

    /**
     * 数据权限Id。
     */
    @ApiModelProperty(value = "数据权限Id", required = true)
    @NotNull(message = "数据权限Id不能为空！", groups = {UpdateGroup.class})
    private Long dataPermId;

    /**
     * 显示名称。
     */
    @ApiModelProperty(value = "显示名称", required = true)
    @NotBlank(message = "数据权限名称不能为空！")
    private String dataPermName;

    /**
     * 数据权限规则类型(0: 全部可见 1: 只看自己 2: 只看本部门 3: 本部门及子部门 4: 多部门及子部门 5: 自定义部门列表)。
     */
    @ApiModelProperty(value = "数据权限规则类型", required = true)
    @NotNull(message = "数据权限规则类型不能为空！")
    @ConstDictRef(constDictClass = DataPermRuleType.class)
    private Integer ruleType;

    /**
     * 部门Id列表(逗号分隔)。
     */
    @ApiModelProperty(hidden = true)
    private String deptIdListString;

    /**
     * 搜索字符串。
     */
    @ApiModelProperty(value = "LIKE 模糊搜索字符串")
    private String searchString;
}