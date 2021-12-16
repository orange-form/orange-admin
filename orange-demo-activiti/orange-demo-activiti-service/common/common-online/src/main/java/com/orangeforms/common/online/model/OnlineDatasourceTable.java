package com.orangeforms.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 数据源及其关联所引用的数据表实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@TableName(value = "zz_online_datasource_table")
public class OnlineDatasourceTable {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 数据源Id。
     */
    @TableField(value = "datasource_id")
    private Long datasourceId;

    /**
     * 数据源关联Id。
     */
    @TableField(value = "relation_id")
    private Long relationId;

    /**
     * 数据表Id。
     */
    @TableField(value = "table_id")
    private Long tableId;
}
