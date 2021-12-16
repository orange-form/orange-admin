package com.orangeforms.common.online.dto;

import com.orangeforms.common.core.validator.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 在线表单页面和数据源多对多关联Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlinePageDatasourceDto {

    /**
     * 主键Id。
     */
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 页面主键Id。
     */
    @NotNull(message = "数据验证失败，页面主键Id不能为空！")
    private Long pageId;

    /**
     * 数据源主键Id。
     */
    @NotNull(message = "数据验证失败，数据源主键Id不能为空！")
    private Long datasourceId;
}
