package com.orange.demo.statsapi.client;

import com.orange.demo.common.core.base.client.BaseFallbackFactory;
import com.orange.demo.common.core.config.FeignConfig;
import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.object.*;
import com.orange.demo.statsapi.dto.StudentActionStatsDto;
import com.orange.demo.statsapi.vo.StudentActionStatsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学生行为统计服务远程数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@FeignClient(
        name = "stats",
        configuration = FeignConfig.class,
        fallbackFactory = StudentActionStatsClient.StudentActionStatsClientFallbackFactory.class)
public interface StudentActionStatsClient extends BaseClient<StudentActionStatsDto, StudentActionStatsVo, Long> {

    /**
     * 基于主键的(In-list)条件获取远程数据接口。
     *
     * @param statsIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象的数据集合。
     */
    @Override
    @PostMapping("/studentActionStats/listByIds")
    ResponseResult<List<StudentActionStatsVo>> listByIds(
            @RequestParam("statsIds") Set<Long> statsIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param statsId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @Override
    @PostMapping("/studentActionStats/getById")
    ResponseResult<StudentActionStatsVo> getById(
            @RequestParam("statsId") Long statsId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否都存在。
     *
     * @param statsIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @PostMapping("/studentActionStats/existIds")
    ResponseResult<Boolean> existIds(@RequestParam("statsIds") Set<Long> statsIds);

    /**
     * 判断主键Id是否存在。
     *
     * @param statsId 参数主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @Override
    @PostMapping("/studentActionStats/existId")
    ResponseResult<Boolean> existId(@RequestParam("statsId") Long statsId);

    /**
     * 删除主键Id关联的对象。
     *
     * @param statsId 主键Id。
     * @return 应答结果对象。
     */
    @Override
    @PostMapping("/studentActionStats/deleteById")
    ResponseResult<Integer> deleteById(@RequestParam("statsId") Long statsId);

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含删除数量。
     */
    @Override
    @PostMapping("/studentActionStats/deleteBy")
    ResponseResult<Integer> deleteBy(@RequestBody StudentActionStatsDto filter);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/studentActionStats/listBy")
    ResponseResult<MyPageData<StudentActionStatsVo>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/studentActionStats/getBy")
    ResponseResult<StudentActionStatsVo> getBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 和listBy接口相比，以Map列表的方式返回的主要目的是，降低服务之间的耦合度。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    @Override
    @PostMapping("/studentActionStats/listMapBy")
    ResponseResult<MyPageData<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据数量。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @Override
    @PostMapping("/studentActionStats/countBy")
    ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @Override
    @PostMapping("/studentActionStats/aggregateBy")
    ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam);

    @Component("StatsStudentActionStatsClientFallbackFactory")
    @Slf4j
    class StudentActionStatsClientFallbackFactory
            extends BaseFallbackFactory<StudentActionStatsDto, StudentActionStatsVo, Long, StudentActionStatsClient> implements StudentActionStatsClient {

        @Override
        public StudentActionStatsClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new StudentActionStatsClientFallbackFactory();
        }
    }
}
