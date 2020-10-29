package com.orange.demo.common.core.object;

import lombok.Data;

import java.util.List;

/**
 * 分页数据的应答返回对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class MyPageData<T> {
    /**
     * 数据列表。
     */
    private List<T> dataList;
    /**
     * 数据总数量。
     */
    private Long totalCount;
}
