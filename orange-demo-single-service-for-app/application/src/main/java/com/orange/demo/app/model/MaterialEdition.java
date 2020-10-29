package com.orange.demo.app.model;

import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * MaterialEdition实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("MaterialEdition实体对象")
@Data
@Table(name = "zz_material_edition")
public class MaterialEdition {

    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id", required = true)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edition_id")
    private Integer editionId;

    /**
     * 教材版本名称。
     */
    @ApiModelProperty(value = "教材版本名称", required = true)
    @NotBlank(message = "数据验证失败，教材版本名称不能为空！")
    @Column(name = "edition_name")
    private String editionName;

    /**
     * 是否正在使用（0：不是，1：是）。
     */
    @ApiModelProperty(value = "是否正在使用（0：不是，1：是）", required = true)
    @NotNull(message = "数据验证失败，是否正在使用（0：不是，1：是）不能为空！")
    private Integer status;
}
