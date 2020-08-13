package com.orange.demo.common.core.base.client;

import com.orange.demo.common.core.object.MyAggregationParam;
import com.orange.demo.common.core.object.MyQueryParam;
import com.orange.demo.common.core.object.ResponseResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 远程调用接口。
 *
 * @param <D> 主DomainDto域数据对象类型。
 * @param <K> 主键类型。
 * @author Orange Team
 * @date 2020-08-08
 */
public interface BaseClient<D, K> {

    /**
     * 基于主键的(in list)获取远程数据接口。
     *
     * @param filterIds 主键Id集合。
     * @param withDict  是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    ResponseResult<List<D>> listByIds(Set<K> filterIds, Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param id       主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    ResponseResult<D> getById(K id, Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否全部存在。
     *
     * @param filterIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    ResponseResult<Boolean> existIds(Set<K> filterIds);

    /**
     * 给定主键Id是否存在。
     *
     * @param id 主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    ResponseResult<Boolean> existId(K id);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    default ResponseResult<List<D>> listBy(MyQueryParam queryParam) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    default ResponseResult<D> getBy(MyQueryParam queryParam) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    default ResponseResult<List<Map<String, Object>>> listMapBy(MyQueryParam queryParam) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取远程主对象中符合查询条件的数据数量。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    default ResponseResult<Integer> countBy(MyQueryParam queryParam) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取远程主对象中符合过滤条件的分组聚合数据。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param aggregationParam 聚合参数。
     * @return 应答结果对象，包含聚合计算后的数据列表。
     */
    default ResponseResult<List<Map<String, Object>>> aggregateBy(MyAggregationParam aggregationParam) {
        throw new UnsupportedOperationException();
    }
}
