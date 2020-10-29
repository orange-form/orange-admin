package com.orange.demo.statsservice.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

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
    @NotNull(message = "数据验证失败，学校Id不能为空！")
    @Id
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 学校名称。
     */
    @NotBlank(message = "数据验证失败，学校名称不能为空！")
    @Column(name = "school_name")
    private String schoolName;

    /**
     * 所在省Id。
     */
    @NotNull(message = "数据验证失败，所在省Id不能为空！")
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @NotNull(message = "数据验证失败，所在城市Id不能为空！")
    @Column(name = "city_id")
    private Long cityId;
}
