package com.orange.demo.courseclassservice.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 行政区划实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_area_code")
public class AreaCode {

    /**
     * 行政区划主键Id
     */
    @Id
    @Column(name = "area_id")
    private Long areaId;

    /**
     * 行政区划名称
     */
    @Column(name = "area_name")
    private String areaName;

    /**
     * 行政区划级别 (1: 省级别 2: 市级别 3: 区级别)
     */
    @Column(name = "area_level")
    private Integer areaLevel;

    /**
     * 父级行政区划Id
     */
    @Column(name = "parent_id")
    private Long parentId;
}
