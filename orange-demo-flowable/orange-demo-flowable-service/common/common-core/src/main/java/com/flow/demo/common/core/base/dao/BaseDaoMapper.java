package com.flow.demo.common.core.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据访问对象的基类。
 *
 * @param <M> 主Model实体对象。
 * @author Jerry
 * @date 2021-06-06
 */
public interface BaseDaoMapper<M> extends BaseMapper<M> {

    /**
     * 根据指定的表名、显示字段列表、过滤条件字符串和分组字段，返回聚合计算后的查询结果。
     *
     * @param selectTable    表名称。
     * @param selectFields   返回字段列表，逗号分隔。
     * @param whereClause    SQL常量形式的条件从句。
     * @param groupBy        分组字段列表，逗号分隔。
     * @return 对象可选字段Map列表。
     */
    @Select("<script>"
            + "SELECT ${selectFields} FROM ${selectTable}"
            + "<where>"
            + "    <if test=\"whereClause != null and whereClause != ''\">"
            + "        AND ${whereClause}"
            + "    </if>"
            + "</where>"
            + "<if test=\"groupBy != null and groupBy != ''\">"
            + "    GROUP BY ${groupBy}"
            + "</if>"
            + "</script>")
    List<Map<String, Object>> getGroupedListByCondition(
            @Param("selectTable") String selectTable,
            @Param("selectFields") String selectFields,
            @Param("whereClause") String whereClause,
            @Param("groupBy") String groupBy);

    /**
     * 根据指定的表名、显示字段列表、过滤条件字符串和排序字符串，返回查询结果。
     *
     * @param selectTable  表名称。
     * @param selectFields 选择的字段列表。
     * @param whereClause  过滤字符串。
     * @param orderBy      排序字符串。
     * @return 查询结果。
     */
    @Select("<script>"
            + "SELECT ${selectFields} FROM ${selectTable}"
            + "<where>"
            + "    <if test=\"whereClause != null and whereClause != ''\">"
            + "        AND ${whereClause}"
            + "    </if>"
            + "</where>"
            + "<if test=\"orderBy != null and orderBy != ''\">"
            + "    ORDER BY ${orderBy}"
            + "</if>"
            + "</script>")
    List<Map<String, Object>> getListByCondition(
            @Param("selectTable") String selectTable,
            @Param("selectFields") String selectFields,
            @Param("whereClause") String whereClause,
            @Param("orderBy") String orderBy);

    /**
     * 用指定过滤条件，计算记录数量。
     *
     * @param selectTable  表名称。
     * @param whereClause  过滤字符串。
     * @return 返回过滤后的数据数量。
     */
    @Select("<script>"
            + "SELECT COUNT(1) FROM ${selectTable}"
            + "<where>"
            + "    <if test=\"whereClause != null and whereClause != ''\">"
            + "        AND ${whereClause}"
            + "    </if>"
            + "</where>"
            + "</script>")
    int getCountByCondition(@Param("selectTable") String selectTable, @Param("whereClause") String whereClause);
}
