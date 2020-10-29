package com.orange.demo.statsinterface.client;

import com.orange.demo.common.core.config.FeignConfig;
import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.*;
import com.orange.demo.statsinterface.dto.StudentActionTransDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学生行为流水服务远程数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@FeignClient(
        name = "stats",
        configuration = FeignConfig.class,
        fallbackFactory = StudentActionTransClient.StudentActionTransClientFallbackFactory.class)
public interface StudentActionTransClient extends BaseClient<StudentActionTransDto, Long> {

    /**
     * 基于主键的(In-list)条件获取远程数据接口。
     *
     * @param transIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象的数据集合。
     */
    @Override
    @PostMapping("/studentActionTrans/listByIds")
    ResponseResult<List<StudentActionTransDto>> listByIds(
            @RequestParam("transIds") Set<Long> transIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param transId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @Override
    @PostMapping("/studentActionTrans/getById")
    ResponseResult<StudentActionTransDto> getById(
            @RequestParam("transId") Long transId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否都存在。
     *
     * @param transIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @PostMapping("/studentActionTrans/existIds")
    ResponseResult<Boolean> existIds(@RequestParam("transIds") Set<Long> transIds);

    /**
     * 判断主键Id是否存在。
     *
     * @param transId 参数主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @Override
    @PostMapping("/studentActionTrans/existId")
    ResponseResult<Boolean> existId(@RequestParam("transId") Long transId);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/studentActionTrans/listBy")
    ResponseResult<List<StudentActionTransDto>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/studentActionTrans/getBy")
    ResponseResult<StudentActionTransDto> getBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 和listBy接口相比，以Map列表的方式返回的主要目的是，降低服务之间的耦合度。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    @Override
    @PostMapping("/studentActionTrans/listMapBy")
    ResponseResult<List<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据数量。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @Override
    @PostMapping("/studentActionTrans/countBy")
    ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @Override
    @PostMapping("/studentActionTrans/aggregateBy")
    ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam);

    @Component("StatsStudentActionTransClientFallbackFactory")
    @Slf4j
    class StudentActionTransClientFallbackFactory implements FallbackFactory<StudentActionTransClient>, StudentActionTransClient {

        @Override
        public ResponseResult<List<StudentActionTransDto>> listByIds(
                Set<Long> transIds, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<StudentActionTransDto> getById(
                Long transId, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existIds(Set<Long> transIds) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existId(Long transId) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<List<StudentActionTransDto>> listBy(MyQueryParam queryParam) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<StudentActionTransDto> getBy(MyQueryParam queryParam) {
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
        public StudentActionTransClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new StudentActionTransClientFallbackFactory();
        }
    }
}
