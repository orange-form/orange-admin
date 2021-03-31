package com.orange.demo.common.core.object;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分组聚合查询参数。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class MyAggregationParam {

    /**
     * 聚合返回数据中，聚合键的常量字段名。
     * 如select groupColumn groupedKey, max(aggregationColumn) aggregatedValue。
     */
    public static final String KEY_NAME = "groupedKey";

    /**
     * 聚合返回数据中，聚合值的常量字段名。
     * 如select groupColumn groupedKey, max(aggregationColumn) aggregatedValue。
     */
    public static final String VALUE_NAME = "aggregatedValue";

    /**
     * 聚合计算是否使用数据权限进行过滤。true表示数据过滤将产生作用，否则SQL中不会包含数据过滤。
     * 目前数据过滤包括数据权限过滤和租户数据过滤。
     */
    private Boolean useDataFilter = true;

    /**
     * 聚合分类，具体数值见AggregationKind。
     */
    private Integer aggregationKind;

    /**
     * 和groupedInFilterValues配合使用。其value集合中的值，需要基于infilterField设定的字段进行(in list)过滤。
     */
    private String inFilterField;

    /**
     * 需要分组执行的 (in list) 数据集合。
     * 在聚合类别为 MANY_TO_MANY 的场景下，将迭代每一个key并分批次执行(in list)过滤，key作为分组值返回。
     */
    private Map<Object, Set<Object>> groupedInFilterValues;

    /**
     * 过滤条件列表。
     */
    private List<MyWhereCriteria> whereCriteriaList;

    /**
     * 分组字段。(Java对象字段名称)，通常用于聚合类别为 ONE_TO_MANY 的场景。
     */
    private String groupField;

    /**
     * 聚合字段。(Java对象字段名称)
     */
    private String aggregationField;

    /**
     * 聚合类型，具体数值见AggregationType对象的常量值。如COUNT、SUM、MIN、MAX、AVG等。
     */
    private Integer aggregationType;
}
