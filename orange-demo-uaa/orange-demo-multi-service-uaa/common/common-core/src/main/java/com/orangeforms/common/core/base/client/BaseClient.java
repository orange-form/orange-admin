package com.orangeforms.common.core.base.client;

import com.orangeforms.common.core.object.*;

import java.util.*;

/**
 * 远程调用接口。
 *
 * @param <D> 主DomainDto域数据对象类型。
 * @param <V> 主DomainVo域数据对象类型。
 * @param <K> 主键类型。
 * @author Jerry
 * @date 2020-08-08
 */
public interface BaseClient<D, V, K> {

    /**
     * 基于主键的(in list)获取远程数据接口。
     *
     * @param filterIds 主键Id集合。
     * @param withDict  是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    ResponseResult<List<V>> listByIds(Set<K> filterIds, Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param id       主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    ResponseResult<V> getById(K id, Boolean withDict);

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
     * 保存或更新数据。
     *
     * @param data 主键Id为null时表示新增数据，否则更新数据。
     * @return 应答结果对象，主键Id。
     */
    default ResponseResult<V> saveNewOrUpdate(D data) {
        throw new UnsupportedOperationException();
    }

    /**
     * 批量新增或保存数据列表。
     *
     * @param dataList 数据列表。主键Id为null时表示新增数据，否则更新数据。
     * @return 应答结果对象。
     */
    default ResponseResult<Void> saveNewOrUpdateBatch(List<D> dataList) {
        throw new UnsupportedOperationException();
    }

    /**
     * 验证指定数据的关联Id数据是否存合法。
     *
     * @param data 数据对象。
     * @return 应答结果对象。
     */
    default ResponseResult<Void> verifyRelatedData(D data) {
        throw new UnsupportedOperationException();
    }

    /**
     * 验证指定数据列表的关联Id数据是否存合法。
     *
     * @param dataList 数据对象列表。
     * @return 应答结果对象。
     */
    default ResponseResult<Void> verifyRelatedDataList(List<D> dataList) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除主键Id关联的对象。
     *
     * @param id 主键Id。
     * @return 应答结果对象。
     */
     default ResponseResult<Integer> deleteById(K id) {
         throw new UnsupportedOperationException();
     }

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含删除数量。
     */
     default ResponseResult<Integer> deleteBy(D filter) {
         throw new UnsupportedOperationException();
     }

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    default ResponseResult<MyPageData<V>> listBy(MyQueryParam queryParam) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    default ResponseResult<V> getBy(MyQueryParam queryParam) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 缺省实现是因为字典类型的远程调用客户端中，不需要实现该方法，因此尽早抛出异常，用户可自行修改。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    default ResponseResult<MyPageData<Map<String, Object>>> listMapBy(MyQueryParam queryParam) {
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

    /**
     * 根据主键Id及其列表数据(not in list)进行过滤，返回给定的数据。返回的对象数据中，仅仅包含实体对象自己的数据，以及配置的字典关联数据。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含分页查询数据列表。
     */
    default ResponseResult<MyPageData<V>> listByNotInList(MyQueryParam queryParam) {
        throw new UnsupportedOperationException();
    }
}
