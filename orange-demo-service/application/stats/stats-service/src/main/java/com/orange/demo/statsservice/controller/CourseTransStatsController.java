package com.orange.demo.statsservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.statsservice.model.*;
import com.orange.demo.statsservice.service.*;
import com.orange.demo.statsinterface.dto.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.annotation.MyRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 课程统计操作控制器类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/courseTransStats")
public class CourseTransStatsController extends BaseController<CourseTransStats, CourseTransStatsDto, Long> {

    @Autowired
    private CourseTransStatsService courseTransStatsService;

    @Override
    protected BaseService<CourseTransStats, CourseTransStatsDto, Long> service() {
        return courseTransStatsService;
    }

    /**
     * 列出符合过滤条件的课程统计列表。
     *
     * @param courseTransStatsDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<JSONObject> list(
            @MyRequestBody("courseTransStatsFilter") CourseTransStatsDto courseTransStatsDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        CourseTransStats courseTransStatsFilter = CourseTransStats.INSTANCE.toModel(courseTransStatsDtoFilter);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, CourseTransStats.class);
        List<CourseTransStats> courseTransStatsList =
                courseTransStatsService.getCourseTransStatsListWithRelation(courseTransStatsFilter, orderBy);
        long totalCount = 0L;
        if (courseTransStatsList instanceof Page) {
            totalCount = ((Page<CourseTransStats>) courseTransStatsList).getTotal();
        }
        // 分页连同对象数据转换copy工作，下面的方法一并完成。
        Tuple2<List<CourseTransStatsDto>, Long> responseData =
                new Tuple2<>(CourseTransStats.INSTANCE.fromModelList(courseTransStatsList), totalCount);
        return ResponseResult.success(MyPageUtil.makeResponseData(responseData));
    }

    /**
     * 分组列出符合过滤条件的课程统计列表。
     *
     * @param courseTransStatsDtoFilter 过滤对象。
     * @param groupParam 分组参数。
     * @param orderParam 排序参数。
     * @param pageParam  分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/listWithGroup")
    public ResponseResult<JSONObject> listWithGroup(
            @MyRequestBody("courseTransStatsFilter") CourseTransStatsDto courseTransStatsDtoFilter,
            @MyRequestBody MyGroupParam groupParam,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        String orderBy = MyOrderParam.buildOrderBy(orderParam, CourseTransStats.class);
        groupParam = MyGroupParam.buildGroupBy(groupParam, CourseTransStats.class);
        if (groupParam == null) {
            return ResponseResult.error(
                    ErrorCodeEnum.INVALID_ARGUMENT_FORMAT, "数据参数错误，分组参数不能为空！");
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        CourseTransStats filter = CourseTransStats.INSTANCE.toModel(courseTransStatsDtoFilter);
        MyGroupCriteria criteria = groupParam.getGroupCriteria();
        List<CourseTransStats> resultList = courseTransStatsService.getGroupedCourseTransStatsListWithRelation(
                filter, criteria.getGroupSelect(), criteria.getGroupBy(), orderBy);
        // 分页连同对象数据转换copy工作，下面的方法一并完成。
        JSONObject responseData = MyPageUtil.makeResponseData(resultList, CourseTransStats.INSTANCE);
        return ResponseResult.success(responseData);
    }

    /**
     * 查看指定课程统计对象详情。
     *
     * @param statsId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<CourseTransStatsDto> view(@RequestParam Long statsId) {
        if (MyCommonUtil.existBlankArgument(statsId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        CourseTransStats courseTransStats =
                courseTransStatsService.getByIdWithRelation(statsId, MyRelationParam.full());
        if (courseTransStats == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        CourseTransStatsDto courseTransStatsDto = CourseTransStats.INSTANCE.fromModel(courseTransStats);
        return ResponseResult.success(courseTransStatsDto);
    }

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param statsIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    @PostMapping("/listByIds")
    public ResponseResult<List<CourseTransStatsDto>> listByIds(
            @RequestParam Set<Long> statsIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(statsIds, withDict, CourseTransStats.INSTANCE);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param statsId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @PostMapping("/getById")
    public ResponseResult<CourseTransStatsDto> getById(
            @RequestParam Long statsId, @RequestParam Boolean withDict) {
        return super.baseGetById(statsId, withDict, CourseTransStats.INSTANCE);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param statsIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @PostMapping("/existIds")
    public ResponseResult<Boolean> existIds(@RequestParam Set<Long> statsIds) {
        return super.baseExistIds(statsIds);
    }

    /**
     * 判断参数列表中指定的主键Id是否存在。仅限于微服务间远程接口调用。
     *
     * @param statsId 主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long statsId) {
        return super.baseExistId(statsId);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/listBy")
    public ResponseResult<List<CourseTransStatsDto>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, CourseTransStats.INSTANCE);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/listMapBy")
    public ResponseResult<List<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListMapBy(queryParam, CourseTransStats.INSTANCE);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/getBy")
    public ResponseResult<CourseTransStatsDto> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, CourseTransStats.INSTANCE);
    }

    /**
     * 获取远程主对象中符合查询条件的数据数量。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @PostMapping("/countBy")
    public ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam) {
        return super.baseCountBy(queryParam);
    }

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @PostMapping("/aggregateBy")
    public ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam) {
        return super.baseAggregateBy(aggregationParam);
    }
}
