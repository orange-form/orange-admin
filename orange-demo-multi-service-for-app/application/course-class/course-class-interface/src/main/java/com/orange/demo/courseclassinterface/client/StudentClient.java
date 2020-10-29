package com.orange.demo.courseclassinterface.client;

import com.orange.demo.common.core.config.FeignConfig;
import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.*;
import com.orange.demo.courseclassinterface.dto.StudentDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学生数据服务远程数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@FeignClient(
        name = "course-class",
        configuration = FeignConfig.class,
        fallbackFactory = StudentClient.StudentClientFallbackFactory.class)
public interface StudentClient extends BaseClient<StudentDto, Long> {

    /**
     * 基于主键的(In-list)条件获取远程数据接口。
     *
     * @param studentIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象的数据集合。
     */
    @Override
    @PostMapping("/student/listByIds")
    ResponseResult<List<StudentDto>> listByIds(
            @RequestParam("studentIds") Set<Long> studentIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param studentId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @Override
    @PostMapping("/student/getById")
    ResponseResult<StudentDto> getById(
            @RequestParam("studentId") Long studentId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否都存在。
     *
     * @param studentIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @PostMapping("/student/existIds")
    ResponseResult<Boolean> existIds(@RequestParam("studentIds") Set<Long> studentIds);

    /**
     * 判断主键Id是否存在。
     *
     * @param studentId 参数主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @Override
    @PostMapping("/student/existId")
    ResponseResult<Boolean> existId(@RequestParam("studentId") Long studentId);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/student/listBy")
    ResponseResult<List<StudentDto>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/student/getBy")
    ResponseResult<StudentDto> getBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 和listBy接口相比，以Map列表的方式返回的主要目的是，降低服务之间的耦合度。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    @Override
    @PostMapping("/student/listMapBy")
    ResponseResult<List<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据数量。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @Override
    @PostMapping("/student/countBy")
    ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @Override
    @PostMapping("/student/aggregateBy")
    ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam);

    @Component("CourseClassStudentClientFallbackFactory")
    @Slf4j
    class StudentClientFallbackFactory implements FallbackFactory<StudentClient>, StudentClient {

        @Override
        public ResponseResult<List<StudentDto>> listByIds(
                Set<Long> studentIds, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<StudentDto> getById(
                Long studentId, Boolean withDict) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existIds(Set<Long> studentIds) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<Boolean> existId(Long studentId) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<List<StudentDto>> listBy(MyQueryParam queryParam) {
            return ResponseResult.error(ErrorCodeEnum.RPC_DATA_ACCESS_FAILED);
        }

        @Override
        public ResponseResult<StudentDto> getBy(MyQueryParam queryParam) {
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
        public StudentClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new StudentClientFallbackFactory();
        }
    }
}
