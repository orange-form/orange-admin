package com.orange.demo.courseclassservice.model;

import com.orange.demo.common.core.annotation.RelationDict;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.courseclassinterface.vo.SchoolInfoVo;
import lombok.Data;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Map;

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
    @NotNull(message = "数据验证失败，所在省份不能为空！")
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 所在城市Id。
     */
    @NotNull(message = "数据验证失败，所在城市不能为空！")
    @Column(name = "city_id")
    private Long cityId;

    @RelationDict(
            masterIdField = "provinceId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> provinceIdDictMap;

    @RelationDict(
            masterIdField = "cityId",
            slaveServiceName = "areaCodeService",
            slaveModelClass = AreaCode.class,
            slaveIdField = "areaId",
            slaveNameField = "areaName")
    @Transient
    private Map<String, Object> cityIdDictMap;

    @Mapper
    public interface SchoolInfoModelMapper extends BaseModelMapper<SchoolInfoVo, SchoolInfo> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param schoolInfoVo 域对象。
         * @return 实体对象。
         */
        @Override
        SchoolInfo toModel(SchoolInfoVo schoolInfoVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param schoolInfo 实体对象。
         * @return 域对象。
         */
        @Override
        SchoolInfoVo fromModel(SchoolInfo schoolInfo);
    }
    public static final SchoolInfoModelMapper INSTANCE = Mappers.getMapper(SchoolInfoModelMapper.class);
}
