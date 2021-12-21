package com.orangeforms.common.core.base.client;

import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * FeignClient 熔断降级处理对象。
 *
 * @param <D> 主DomainDto域数据对象类型。
 * @param <V> 主DomainVo域数据对象类型。
 * @param <K> 主键类型。
 * @param <T> Feign客户端对象类型。
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public abstract class BaseFallbackFactory<D, V, K, T extends BaseClient<D, V, K>>
        implements FallbackFactory<T>, BaseClient<D, V, K> {

    @Override
    public ResponseResult<List<V>> listByIds(Set<K> idSet, Boolean withDict) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<V> getById(K id, Boolean withDict) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Boolean> existIds(Set<K> idSet) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Boolean> existId(K id) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<V> saveNewOrUpdate(D data) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Void> saveNewOrUpdateBatch(List<D> dataList) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Void> verifyRelatedData(D data) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Void> verifyRelatedDataList(List<D> dataList) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Integer> deleteById(K id) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Integer> deleteBy(D filter) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<MyPageData<V>> listBy(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<V> getBy(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<MyPageData<Map<String, Object>>> listMapBy(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Integer> countBy(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<List<Map<String, Object>>> aggregateBy(MyAggregationParam aggregationParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<MyPageData<V>> listByNotInList(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }
}
