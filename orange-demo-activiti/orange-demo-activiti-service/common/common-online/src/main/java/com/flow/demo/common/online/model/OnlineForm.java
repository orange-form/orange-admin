package com.flow.demo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.flow.demo.common.core.annotation.*;
import com.flow.demo.common.core.base.mapper.BaseModelMapper;
import com.flow.demo.common.online.model.constant.FormType;
import com.flow.demo.common.online.vo.OnlineFormVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_form")
public class OnlineForm {

    /**
     * 主键Id。
     */
    @TableId(value = "form_id")
    private Long formId;

    /**
     * 页面id。
     */
    @TableField(value = "page_id")
    private Long pageId;

    /**
     * 表单编码。
     */
    @TableField(value = "form_code")
    private String formCode;

    /**
     * 表单名称。
     */
    @TableField(value = "form_name")
    private String formName;

    /**
     * 表单类别。
     */
    @TableField(value = "form_kind")
    private Integer formKind;

    /**
     * 表单类型。
     */
    @TableField(value = "form_type")
    private Integer formType;

    /**
     * 表单主表id。
     */
    @TableField(value = "master_table_id")
    private Long masterTableId;

    /**
     * 表单组件JSON。
     */
    @TableField(value = "widget_json")
    private String widgetJson;

    /**
     * 表单参数JSON。
     */
    @TableField(value = "params_json")
    private String paramsJson;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    @RelationOneToOne(
            masterIdField = "masterTableId",
            slaveServiceName = "onlineTableService",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId")
    @TableField(exist = false)
    private OnlineTable onlineTable;

    @RelationDict(
            masterIdField = "masterTableId",
            slaveServiceName = "onlineTableService",
            equalOneToOneRelationField = "onlineTable",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId",
            slaveNameField = "modelName")
    @TableField(exist = false)
    private Map<String, Object> masterTableIdDictMap;

    @RelationConstDict(
            masterIdField = "formType",
            constantDictClass = FormType.class)
    @TableField(exist = false)
    private Map<String, Object> formTypeDictMap;

    @Mapper
    public interface OnlineFormModelMapper extends BaseModelMapper<OnlineFormVo, OnlineForm> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param onlineFormVo 域对象。
         * @return 实体对象。
         */
        @Mapping(target = "onlineTable", expression = "java(mapToBean(onlineFormVo.getOnlineTable(), com.flow.demo.common.online.model.OnlineTable.class))")
        @Override
        OnlineForm toModel(OnlineFormVo onlineFormVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param onlineForm 实体对象。
         * @return 域对象。
         */
        @Mapping(target = "onlineTable", expression = "java(beanToMap(onlineForm.getOnlineTable(), false))")
        @Override
        OnlineFormVo fromModel(OnlineForm onlineForm);
    }
    public static final OnlineFormModelMapper INSTANCE = Mappers.getMapper(OnlineFormModelMapper.class);
}
