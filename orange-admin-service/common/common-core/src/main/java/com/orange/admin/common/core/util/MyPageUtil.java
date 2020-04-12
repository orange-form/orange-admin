package com.orange.admin.common.core.util;

import cn.jimmyshi.beanquery.BeanQuery;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * 生成带有分页信息的数据列表
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
public class MyPageUtil {

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param dataList 数据列表，该参数必须是调用PageHelper.startPage之后，立即执行mybatis查询操作的结果集。
     * @param includeFields 结果集中需要返回到前端的字段，多个字段之间逗号分隔。
     * @return 返回只是包含includeFields字段的数据列表，以及结果集TotalCount。
     */
    public static <T> JSONObject makeResponseData(List<T> dataList, String includeFields) {
        JSONObject pageData = new JSONObject();
        pageData.put("dataList", BeanQuery.select(includeFields).from(dataList).execute());
        if (dataList instanceof Page) {
            pageData.put("totalCount", ((Page<?>)dataList).getTotal());
        }
        return pageData;
    }

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param dataList 数据列表，该参数必须是调用PageHelper.startPage之后，立即执行mybatis查询操作的结果集。
     * @return 返回结果集和TotalCount。
     */
    public static <T> JSONObject makeResponseData(List<T> dataList) {
        JSONObject pageData = new JSONObject();
        pageData.put("dataList", dataList);
        if (dataList instanceof Page) {
            pageData.put("totalCount", ((Page<?>)dataList).getTotal());
        }
        return pageData;
    }

    /**
     * 用户构建带有分页信息的数据列表。
     *
     * @param dataList 数据列表，该参数必须是调用PageHelper.startPage之后，立即执行mybatis查询操作的结果集。
     * @param totalCount 总数量。
     * @return 返回结果集和TotalCount。
     */
    public static <T> JSONObject makeResponseData(List<T> dataList, Long totalCount) {
        JSONObject pageData = new JSONObject();
        pageData.put("dataList", dataList);
        if (totalCount != null) {
            pageData.put("totalCount", totalCount);
        }
        return pageData;
    }

}
