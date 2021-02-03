package com.orange.demo.courseclassservice.model;

import lombok.Data;
import javax.persistence.*;

/**
 * MaterialEdition实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_material_edition")
public class MaterialEdition {

    /**
     * 主键Id。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edition_id")
    private Integer editionId;

    /**
     * 教材版本名称。
     */
    @Column(name = "edition_name")
    private String editionName;

    /**
     * 是否正在使用（0：不是，1：是）。
     */
    private Integer status;
}
