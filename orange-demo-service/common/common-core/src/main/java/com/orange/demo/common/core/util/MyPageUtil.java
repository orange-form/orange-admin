package com.orange.demo.common.core.util;

import cn.jimmyshi.beanquery.BeanQuery;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.orange.demo.common.core.base.mapper.BaseModelMapper;
import com.orange.demo.common.core.object.Tuple2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 生成带有分页信息的数据列表
 *
 * @author Orange Team
 * @date 2020-08-08
 */
public class MyPageUtil {

    private static final String DATA_LIST_LITERAL = "dataList";
    private static final String TOTAL_COUNT_LITERAL = "totalCount";

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param dataList      数据列表，该参数必须是调用PageMethod.startPage之后，立即执行mybatis查询操作的结果集。
     * @param includeFields 结果集中需要返回到前端的字段，多个字段之间逗号分隔。
     * @param <T>           源数据类型。
     * @return 返回只是包含includeFields字段的数据列表，以及结果集TotalCount。
     */
    public static <T> JSONObject makeResponseData(List<T> dataList, String includeFields) {
        JSONObject pageData = new JSONObject();
        pageData.put(DATA_LIST_LITERAL, BeanQuery.select(includeFields).from(dataList).execute());
        if (dataList instanceof Page) {
            pageData.put(TOTAL_COUNT_LITERAL, ((Page<?>)dataList).getTotal());
        }
        return pageData;
    }

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param dataList 数据列表，该参数必须是调用PageMethod.startPage之后，立即执行mybatis查询操作的结果集。
     * @param <T>      源数据类型。
     * @return 返回结果集和TotalCount。
     */
    public static <T> JSONObject makeResponseData(List<T> dataList) {
        JSONObject pageData = new JSONObject();
        pageData.put(DATA_LIST_LITERAL, dataList);
        if (dataList instanceof Page) {
            pageData.put(TOTAL_COUNT_LITERAL, ((Page<?>)dataList).getTotal());
        }
        return pageData;
    }

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param dataList   数据列表，该参数必须是调用PageMethod.startPage之后，立即执行mybatis查询操作的结果集。
     * @param totalCount 总数量。
     * @param <T>        源数据类型。
     * @return 返回结果集和TotalCount。
     */
    public static <T> JSONObject makeResponseData(List<T> dataList, Long totalCount) {
        JSONObject pageData = new JSONObject();
        pageData.put(DATA_LIST_LITERAL, dataList);
        if (totalCount != null) {
            pageData.put(TOTAL_COUNT_LITERAL, totalCount);
        }
        return pageData;
    }

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param dataList    实体对象数据列表。
     * @param modelMapper 实体对象到Dto对象的数据映射器。
     * @param <D>         Dto对象类型。
     * @param <T>         实体对象类型。
     * @return JSON对象中将包含转换后的Dto数据列表，如果dataList是Page分页对象，返回数据中要将包含分页信息。
     */
    public static <D, T> JSONObject makeResponseData(List<T> dataList, BaseModelMapper<D, T> modelMapper) {
        if (CollectionUtils.isEmpty(dataList)) {
            // 这里需要构建分页数据对象，统一前端数据格式
            return MyPageUtil.makeResponseData(dataList);
        }
        long totalCount = 0L;
        if (dataList instanceof Page) {
            totalCount = ((Page<T>) dataList).getTotal();
        }
        return MyPageUtil.makeResponseData(modelMapper.fromModelList(dataList), totalCount);
    }

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param responseData 第一个数据时数据列表，第二个是列表数量。
     * @param <T>          源数据类型。
     * @return 返回结果集和TotalCount。
     */
    public static <T> JSONObject makeResponseData(Tuple2<List<T>, Long> responseData) {
        return makeResponseData(responseData.getFirst(), responseData.getSecond());
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private MyPageUtil() {
    }
}
