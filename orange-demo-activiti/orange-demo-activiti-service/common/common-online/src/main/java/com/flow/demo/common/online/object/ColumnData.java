package com.flow.demo.common.online.object;

import com.flow.demo.common.online.model.OnlineColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表字段数据对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnData {

    /**
     * 在线表字段对象。
     */
    private OnlineColumn column;

    /**
     * 字段值。
     */
    private Object columnValue;
}
