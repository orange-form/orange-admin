package com.orange.demo.statsservice.model;

import lombok.Data;
import javax.persistence.*;

/**
 * SchoolInfo实体对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
@Table(name = "zz_school_info")
public class SchoolInfo {

    /**
     * 学校Id。
     */
    @Id
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 学校名称。
     */
    @Column(name = "school_name")
    private String schoolName;

    /**
     * 所在省Id。
     */
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @Column(name = "city_id")
    private Long cityId;
}
