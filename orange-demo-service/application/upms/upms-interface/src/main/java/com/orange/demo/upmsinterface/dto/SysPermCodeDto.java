package com.orange.demo.upmsinterface.dto;

import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.upmsinterface.constant.SysPermCodeType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 权限字Dto。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Data
public class SysPermCodeDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "权限字Id不能为空！", groups = {UpdateGroup.class})
    private Long permCodeId;

    /**
     * 权限字标识(一般为有含义的英文字符串)。
     */
    @NotBlank(message = "权限字编码不能为空！")
    private String permCode;

    /**
     * 上级权限字Id。
     */
    private Long parentId;

    /**
     * 权限类型(0: 表单 1: UI片段 2: 操作)。
     */
    @NotNull(message = "权限字类型不能为空！")
    @ConstDictRef(constDictClass = SysPermCodeType.class, message = "数据验证失败，权限类型为无效值！")
    private Integer permCodeType;

    /**
     * 显示名称。
     */
    @NotBlank(message = "权限字显示名称不能为空！")
    private String showName;

    /**
     * 显示顺序(数值越小，越靠前)。
     */
    @NotNull(message = "权限字显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 权限字与权限资源关联对象列表。
     */
    private List<Map<String, Object>> sysPermCodePermList;
}