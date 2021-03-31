package com.orange.demo.courseclassapi.client;

import com.orange.demo.common.core.base.client.BaseFallbackFactory;
import com.orange.demo.common.core.config.FeignConfig;
import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.object.*;
import com.orange.demo.courseclassapi.dto.StudentClassDto;
import com.orange.demo.courseclassapi.vo.StudentClassVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 班级数据服务远程数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@FeignClient(
        name = "course-class",
        configuration = FeignConfig.class,
        fallbackFactory = StudentClassClient.StudentClassClientFallbackFactory.class)
public interface StudentClassClient extends BaseClient<StudentClassDto, StudentClassVo, Long> {

    /**
     * 基于主键的(In-list)条件获取远程数据接口。
     *
     * @param classIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象的数据集合。
     */
    @Override
    @PostMapping("/studentClass/listByIds")
    ResponseResult<List<StudentClassVo>> listByIds(
            @RequestParam("classIds") Set<Long> classIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param classId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @Override
    @PostMapping("/studentClass/getById")
    ResponseResult<StudentClassVo> getById(
            @RequestParam("classId") Long classId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否都存在。
     *
     * @param classIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @PostMapping("/studentClass/existIds")
    ResponseResult<Boolean> existIds(@RequestParam("classIds") Set<Long> classIds);

    /**
     * 判断主键Id是否存在。
     *
     * @param classId 参数主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @Override
    @PostMapping("/studentClass/existId")
    ResponseResult<Boolean> existId(@RequestParam("classId") Long classId);

    /**
     * 删除主键Id关联的对象。
     *
     * @param classId 主键Id。
     * @return 应答结果对象。
     */
    @Override
    @PostMapping("/studentClass/deleteById")
    ResponseResult<Integer> deleteById(@RequestParam("classId") Long classId);

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含删除数量。
     */
    @Override
    @PostMapping("/studentClass/deleteBy")
    ResponseResult<Integer> deleteBy(@RequestBody StudentClassDto filter);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/studentClass/listBy")
    ResponseResult<MyPageData<StudentClassVo>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/studentClass/getBy")
    ResponseResult<StudentClassVo> getBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 和listBy接口相比，以Map列表的方式返回的主要目的是，降低服务之间的耦合度。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    @Override
    @PostMapping("/studentClass/listMapBy")
    ResponseResult<MyPageData<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据数量。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @Override
    @PostMapping("/studentClass/countBy")
    ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @Override
    @PostMapping("/studentClass/aggregateBy")
    ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam);

    @Component("CourseClassStudentClassClientFallbackFactory")
    @Slf4j
    class StudentClassClientFallbackFactory
            extends BaseFallbackFactory<StudentClassDto, StudentClassVo, Long, StudentClassClient> implements StudentClassClient {

        @Override
        public StudentClassClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new StudentClassClientFallbackFactory();
        }
    }
}
