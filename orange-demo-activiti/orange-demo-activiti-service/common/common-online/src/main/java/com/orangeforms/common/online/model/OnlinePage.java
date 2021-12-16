package com.orangeforms.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import com.orangeforms.common.core.annotation.RelationConstDict;
import com.orangeforms.common.core.base.mapper.BaseModelMapper;
import com.orangeforms.common.online.model.constant.PageStatus;
import com.orangeforms.common.online.model.constant.PageType;
import com.orangeforms.common.online.vo.OnlinePageVo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单所在页面实体对象。这里我们可以把页面理解为表单的容器。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_page")
public class OnlinePage {

    /**
     * 主键Id。
     */
    @TableId(value = "page_id")
    private Long pageId;

    /**
     * 页面编码。
     */
    @TableField(value = "page_code")
    private String pageCode;

    /**
     * 页面名称。
     */
    @TableField(value = "page_name")
    private String pageName;

    /**
     * 页面类型。
     */
    @TableField(value = "page_type")
    private Integer pageType;

    /**
     * 页面编辑状态。
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否发布。
     */
    @TableField(value = "published")
    private Boolean published;

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

    @RelationConstDict(
            masterIdField = "pageType",
            constantDictClass = PageType.class)
    @TableField(exist = false)
    private Map<String, Object> pageTypeDictMap;

    @RelationConstDict(
            masterIdField = "status",
            constantDictClass = PageStatus.class)
    @TableField(exist = false)
    private Map<String, Object> statusDictMap;

    @Mapper
    public interface OnlinePageModelMapper extends BaseModelMapper<OnlinePageVo, OnlinePage> {
        /**
         * 转换Vo对象到实体对象。
         *
         * @param onlinePageVo 域对象。
         * @return 实体对象。
         */
        @Override
        OnlinePage toModel(OnlinePageVo onlinePageVo);
        /**
         * 转换实体对象到VO对象。
         *
         * @param onlinePage 实体对象。
         * @return 域对象。
         */
        @Override
        OnlinePageVo fromModel(OnlinePage onlinePage);
    }
    public static final OnlinePageModelMapper INSTANCE = Mappers.getMapper(OnlinePageModelMapper.class);
}
