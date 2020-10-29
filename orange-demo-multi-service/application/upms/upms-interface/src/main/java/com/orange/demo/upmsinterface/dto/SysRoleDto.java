package com.orange.demo.upmsinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 角色Dto。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("角色Dto")
@Data
public class SysRoleDto {

    /**
     * 角色Id。
     */
    @ApiModelProperty(value = "角色Id", required = true)
    @NotNull(message = "角色Id不能为空！", groups = {UpdateGroup.class})
    private Long roleId;

    /**
     * 角色名称。
     */
    @ApiModelProperty(value = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空！")
    private String roleName;

    /**
     * 创建者Id。
     */
    @ApiModelProperty(value = "创建者Id")
    private Long createUserId;

    /**
     * 创建者显示名称。
     */
    @ApiModelProperty(value = "创建者显示名称")
    private String createUsername;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 角色与菜单关联对象列表。
     */
    @ApiModelProperty(hidden = true)
    private List<Map<String, Object>> sysRoleMenuList;

    @ApiModelProperty(value = "创建时间开始查询时间")
    private String createTimeStart;

    @ApiModelProperty(value = "创建时间结束查询时间")
    private String createTimeEnd;

    @ApiModelProperty(value = "LIKE 模糊搜索字符串")
    private String searchString;
}
