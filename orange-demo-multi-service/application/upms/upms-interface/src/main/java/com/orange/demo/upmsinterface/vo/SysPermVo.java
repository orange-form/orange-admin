package com.orange.demo.upmsinterface.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

/**
 * 权限资源VO。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiModel("权限资源VO")
@Data
public class SysPermVo {

    /**
     * 权限资源Id。
     */
    @ApiModelProperty(value = "权限资源Id")
    private Long permId;

    /**
     * 权限资源名称。
     */
    @ApiModelProperty(value = "权限资源名称")
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
    private Long moduleId;

    /**
     * 关联的URL。
     */
    @ApiModelProperty(value = "关联的URL")
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @ApiModelProperty(value = "显示顺序")
    private Integer showOrder;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 模块Id的字典关联数据。
     */
    @ApiModelProperty(value = "模块Id的字典关联数据")
    private Map<String, Object> moduleIdDictMap;
}