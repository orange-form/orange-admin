package com.orangeforms.courseclassservice.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * MaterialEdition实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@TableName(value = "zz_material_edition")
public class MaterialEdition {

    /**
     * 主键Id。
     */
    @TableId(value = "edition_id", type = IdType.AUTO)
    private Integer editionId;

    /**
     * 教材版本名称。
     */
    @TableField(value = "edition_name")
    private String editionName;

    /**
     * 是否正在使用（0：不是，1：是）。
     */
    private Integer status;
}
