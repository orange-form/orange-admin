package com.orange.demo.common.core.base.client;

import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.MyAggregationParam;
import com.orange.demo.common.core.object.MyQueryParam;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.common.core.object.Tuple2;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * FeignClient 熔断降级处理对象。
 *
 * @param <D> 实体对象类型。
 * @param <K> 主键类型。
 * @param <T> Feign客户端对象类型。
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
public abstract class BaseFallbackFactory<D, K, T extends BaseClient<D, K>>
        implements FallbackFactory<T>, BaseClient<D, K> {

    @Override
    public ResponseResult<List<D>> listByIds(Set<K> idSet, Boolean withDict) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<D> getById(K id, Boolean withDict) {
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
    public ResponseResult<Void> delete(K id) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<Integer> deleteBy(D filter) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<List<D>> listBy(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<D> getBy(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }

    @Override
    public ResponseResult<List<Map<String, Object>>> listMapBy(MyQueryParam queryParam) {
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
    public ResponseResult<Tuple2<List<D>, K>> listByNotInList(MyQueryParam queryParam) {
        return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
    }
}
