package com.orangeforms.common.core.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * 分页数据的应答返回对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPageData<T> {
    /**
     * 数据列表。
     */
    private List<T> dataList;
    /**
     * 数据总数量。
     */
    private Long totalCount;

    /**
     * 为了保持前端的数据格式兼容性，在没有数据的时候，需要返回空分页对象。
     * @return 空分页对象。
     */
    public static <T> MyPageData<T> emptyPageData() {
        return new MyPageData<>(new LinkedList<>(), 0L);
    }
}
