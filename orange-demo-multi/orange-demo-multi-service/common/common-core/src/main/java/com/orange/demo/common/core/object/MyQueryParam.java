package com.orange.demo.common.core.object;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;

import java.util.*;

/**
 * 查询参数。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class MyQueryParam {

    /**
     * Select中返回的Java属性列表。
     */
    private List<String> selectFieldList;

    /**
     * 用于数据过滤的DTO对象。
     */
    private Map<String, Object> filterMap;

    /**
     * 聚合计算是否使用数据权限进行过滤。true表示数据过滤将产生作用，否则SQL中不会包含数据过滤。
     * 目前数据过滤包括数据权限过滤和租户数据过滤。
     */
    private Boolean useDataFilter = true;

    /**
     * (In-list) 实体对象中的过滤字段(而非数据表列名)，需和下面的inFilterValues字段一起使用。
     * NOTE: MyWhereCriteria中的IN类型过滤条件，完全可以替代该字段。之所以保留主要是为了保证更好的接口可读性。
     */
    private String inFilterField;

    /**
     * (In-list) 过滤数据集合。
     */
    private Set<?> inFilterValues;

    /**
     * 过滤条件列表。
     */
    private List<MyWhereCriteria> criteriaList;

    /**
     * 排序对象。
     */
    private MyOrderParam orderParam;

    /**
     * 分页对象。
     */
    private MyPageParam pageParam;

    /**
     * 是否包含关联字典数据
     */
    private Boolean withDict = false;

    /**
     * 缺省构造函数。
     */
    public MyQueryParam() {

    }

    /**
     * 构造函数。
     *
     * @param withDict 是否关联字典数据。
     */
    public MyQueryParam(Boolean withDict) {
        this.withDict = withDict;
    }

    /**
     * 将参数中Dto类型的过滤对象转换为内部的Map对象，便于服务间传输。
     *
     * @param filterDto 过滤对象。
     * @param <T>       Dto对象的类型。
     */
    public <T> void setFilterDto(T filterDto) {
        this.filterMap = BeanUtil.beanToMap(filterDto);
    }

    /**
     * 将内部的过滤Map对象转换为指定类型的Dto对象并返回。
     *
     * @param filterClazz Dto对象的Class对象。
     * @param <T>         Dto对象的类型。
     * @return 如果filterMap字段为空，则返回空对象。
     */
    public <T> T getFilterDto(Class<T> filterClazz) {
        if (filterMap == null) {
            return null;
        }
        return BeanUtil.toBeanIgnoreError(this.filterMap, filterClazz);
    }

    /**
     * 添加自定义过滤条件。
     *
     * @param criteria 自定义过滤条件。
     */
    public void addCriteriaList(MyWhereCriteria criteria) {
        if (this.criteriaList == null) {
            criteriaList = new LinkedList<>();
        }
        criteriaList.add(criteria);
    }
}
