package com.flow.demo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 在线表单和数据源多对多关联实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_form_datasource")
public class OnlineFormDatasource {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 表单Id。
     */
    @TableField(value = "form_id")
    private Long formId;

    /**
     * 数据源Id。
     */
    @TableField(value = "datasource_id")
    private Long datasourceId;
}
