package com.flow.demo.common.online.vo;

import lombok.Data;

/**
 * 在线表单页面和数据源多对多关联VO对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class OnlinePageDatasourceVo {

    /**
     * 主键Id。
     */
    private Long id;

    /**
     * 页面主键Id。
     */
    private Long pageId;

    /**
     * 数据源主键Id。
     */
    private Long datasourceId;
}
