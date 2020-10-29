package com.orange.demo.app.model;

import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.validator.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Map;

/**
 * SchoolInfo实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@ApiModel("SchoolInfo实体对象")
@Data
@Table(name = "zz_school_info")
public class SchoolInfo {

    /**
     * 学校Id。
     */
    @ApiModelProperty(value = "学校Id", required = true)
    @NotNull(message = "数据验证失败，学校Id不能为空！", groups = {UpdateGroup.class})
    @Id
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 学校名称。
     */
    @ApiModelProperty(value = "学校名称", required = true)
    @NotBlank(message = "数据验证失败，学校名称不能为空！")
    @Column(name = "school_name")
    private String schoolName;

    /**
     * 所在省Id。
     */
    @ApiModelProperty(value = "所在省Id", required = true)
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @ApiModelProperty(value = "所在城市Id", required = true)
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    @Column(name = "city_id")
    private Long cityId;

    @ApiModelProperty(hidden = true)
    @RelationDict(
            masterIdField = "provinceId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> provinceIdDictMap;

    @ApiModelProperty(hidden = true)
    @RelationDict(
            masterIdField = "cityId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> cityIdDictMap;
}
