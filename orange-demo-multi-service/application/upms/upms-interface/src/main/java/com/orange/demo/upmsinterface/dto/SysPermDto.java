package com.orange.demo.upmsinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 权限资源Dto。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("权限资源Dto")
@Data
public class SysPermDto {

    /**
     * 权限资源Id。
     */
    @ApiModelProperty(value = "权限资源Id", required = true)
    @NotNull(message = "权限Id不能为空！", groups = {UpdateGroup.class})
    private Long permId;

    /**
     * 权限资源名称。
     */
    @ApiModelProperty(value = "权限资源名称", required = true)
    @NotBlank(message = "权限资源名称不能为空！")
    private String permName;

    /**
     * shiro格式的权限字，如(upms:sysUser:add)。
     */
    @ApiModelProperty(value = "权限字")
    private String permCode;

    /**
     * 权限所在的权限模块Id。
     */
    @ApiModelProperty(value = "权限所在的权限模块Id")
    @NotNull(message = "权限模块Id不能为空！")
    private Long moduleId;

    /**
     * 关联的URL。
     */
    @ApiModelProperty(value = "关联的URL", required = true)
    @NotBlank(message = "权限关联的url不能为空！")
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @ApiModelProperty(value = "显示顺序", required = true)
    @NotNull(message = "权限显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}