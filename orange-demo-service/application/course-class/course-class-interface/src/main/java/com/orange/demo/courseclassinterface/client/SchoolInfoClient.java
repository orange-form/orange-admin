package com.orange.demo.courseclassinterface.client;

import com.orange.demo.common.core.config.FeignConfig;
import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.*;
import com.orange.demo.courseclassinterface.dto.SchoolInfoDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 校区数据服务远程数据操作访问接口。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@FeignClient(
        name = "course-class",
        configuration = FeignConfig.class,
        fallbackFactory = SchoolInfoClient.SchoolInfoClientFallbackFactory.class)
public interface SchoolInfoClient extends BaseClient<SchoolInfoDto, Long> {

    /**
     * 基于主键的(In-list)条件获取远程数据接口。
     *
     * @param schoolIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象的数据集合。
     */
    @Override
    @PostMapping("/schoolInfo/listByIds")
    ResponseResult<List<SchoolInfoDto>> listByIds(
            @RequestParam("schoolIds") Set<Long> schoolIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param schoolId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @Override
    @PostMapping("/schoolInfo/getById")
    ResponseResult<SchoolInfoDto> getById(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否都存在。
     *
     * @param schoolIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @PostMapping("/schoolInfo/existIds")
    ResponseResult<Boolean> existIds(@RequestParam("schoolIds") Set<Long> schoolIds);

    /**
     * 判断主键Id是否存在。
     *
     * @param schoolId 参数主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @Override
    @PostMapping("/schoolInfo/existId")
    ResponseResult<Boolean> existId(@RequestParam("schoolId") Long schoolId);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/schoolInfo/listBy")
    ResponseResult<List<SchoolInfoDto>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/schoolInfo/getBy")
    ResponseResult<SchoolInfoDto> getBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 和listBy接口相比，以Map列表的方式返回的主要目的是，降低服务之间的耦合度。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    @Override
    @PostMapping("/schoolInfo/listMapBy")
    ResponseResult<List<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据数量。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @Override
    @PostMapping("/schoolInfo/countBy")
    ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @Override
    @PostMapping("/schoolInfo/aggregateBy")
    ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam);

    @Component("CourseClassSchoolInfoClientFallbackFactory")
    @Slf4j
    class SchoolInfoClientFallbackFactory implements FallbackFactory<SchoolInfoClient>, SchoolInfoClient {

        @Override
        public ResponseResult<List<SchoolInfoDto>> listByIds(
                Set<Long> schoolIds, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<SchoolInfoDto> getById(
                Long schoolId, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existIds(Set<Long> schoolIds) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existId(Long schoolId) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<List<SchoolInfoDto>> listBy(MyQueryParam queryParam) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<SchoolInfoDto> getBy(MyQueryParam queryParam) {
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
        public SchoolInfoClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new SchoolInfoClientFallbackFactory();
        }
    }
}
