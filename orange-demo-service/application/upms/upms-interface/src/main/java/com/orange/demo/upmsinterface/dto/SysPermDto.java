package com.orange.demo.upmsinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * 权限资源Dto。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Data
public class SysPermDto {

    /**
     * 权限Id。
     */
    @NotNull(message = "权限Id不能为空！", groups = {UpdateGroup.class})
    private Long permId;

    /**
     * 权限名称。
     */
    @NotBlank(message = "权限名称不能为空！")
    private String permName;

    /**
     * shiro格式的权限码，如(upms:sysUser:add)。
     */
    private String permCode;

    /**
     * 权限所在的权限模块Id。
     */
    @NotNull(message = "权限模块Id不能为空！")
    private Long moduleId;

    /**
     * 关联的URL。
     */
    @NotBlank(message = "权限关联的url不能为空！")
    private String url;

    /**
     * 权限在当前模块下的顺序，由小到大。
     */
    @NotNull(message = "权限显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 模块Id的字典关联数据。
     */
    private Map<String, Object> moduleIdDictMap;
}