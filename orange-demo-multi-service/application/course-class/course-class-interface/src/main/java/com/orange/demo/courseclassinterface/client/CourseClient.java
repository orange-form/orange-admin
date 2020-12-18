package com.orange.demo.courseclassinterface.client;

import com.orange.demo.common.core.base.client.BaseFallbackFactory;
import com.orange.demo.common.core.config.FeignConfig;
import com.orange.demo.common.core.base.client.BaseClient;
import com.orange.demo.common.core.object.*;
import com.orange.demo.courseclassinterface.dto.CourseDto;
import com.orange.demo.courseclassinterface.vo.CourseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 课程数据服务远程数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@FeignClient(
        name = "course-class",
        configuration = FeignConfig.class,
        fallbackFactory = CourseClient.CourseClientFallbackFactory.class)
public interface CourseClient extends BaseClient<CourseDto, CourseVo, Long> {

    /**
     * 基于主键的(In-list)条件获取远程数据接口。
     *
     * @param courseIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象的数据集合。
     */
    @Override
    @PostMapping("/course/listByIds")
    ResponseResult<List<CourseVo>> listByIds(
            @RequestParam("courseIds") Set<Long> courseIds,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 基于主键Id，获取远程对象。
     *
     * @param courseId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @Override
    @PostMapping("/course/getById")
    ResponseResult<CourseVo> getById(
            @RequestParam("courseId") Long courseId,
            @RequestParam("withDict") Boolean withDict);

    /**
     * 判断参数列表中指定的主键Id，是否都存在。
     *
     * @param courseIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @Override
    @PostMapping("/course/existIds")
    ResponseResult<Boolean> existIds(@RequestParam("courseIds") Set<Long> courseIds);

    /**
     * 判断主键Id是否存在。
     *
     * @param courseId 参数主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @Override
    @PostMapping("/course/existId")
    ResponseResult<Boolean> existId(@RequestParam("courseId") Long courseId);

    /**
     * 删除主键Id关联的对象。
     *
     * @param courseId 主键Id。
     * @return 应答结果对象。
     */
    @Override
    @PostMapping("/course/delete")
    ResponseResult<Void> delete(@RequestParam("courseId") Long courseId);

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含删除数量。
     */
    @Override
    @PostMapping("/course/deleteBy")
    ResponseResult<Integer> deleteBy(@RequestBody CourseDto filter);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象集合。
     */
    @Override
    @PostMapping("/course/listBy")
    ResponseResult<MyPageData<CourseVo>> listBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的单条数据对象。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含实体对象。
     */
    @Override
    @PostMapping("/course/getBy")
    ResponseResult<CourseVo> getBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据列表。
     * 和listBy接口相比，以Map列表的方式返回的主要目的是，降低服务之间的耦合度。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含主对象集合。
     */
    @Override
    @PostMapping("/course/listMapBy")
    ResponseResult<MyPageData<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程主对象中符合查询条件的数据数量。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @Override
    @PostMapping("/course/countBy")
    ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam);

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @Override
    @PostMapping("/course/aggregateBy")
    ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam);

    @Component("CourseClassCourseClientFallbackFactory")
    @Slf4j
    class CourseClientFallbackFactory
            extends BaseFallbackFactory<CourseDto, CourseVo, Long, CourseClient> implements CourseClient {

        @Override
        public CourseClient create(Throwable throwable) {
            log.error("Exception For Feign Remote Call.", throwable);
            return new CourseClientFallbackFactory();
        }
    }
}
