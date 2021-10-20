package com.flow.demo.common.online.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单所在页面VO对象。这里我们可以把页面理解为表单的容器。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlinePageVo {

    /**
     * 主键Id。
     */
    private Long pageId;

    /**
     * 页面编码。
     */
    private String pageCode;

    /**
     * 页面名称。
     */
    private String pageName;

    /**
     * 页面类型。
     */
    private Integer pageType;

    /**
     * 页面编辑状态。
     */
    private Integer status;

    /**
     * 是否发布。
     */
    private Boolean published;

    /**
     * 更新时间。
     */
    private Date updateTime;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * pageType 常量字典关联数据。
     */
    private Map<String, Object> pageTypeDictMap;

    /**
     * status 常量字典关联数据。
     */
    private Map<String, Object> statusDictMap;
}
