package com.orange.demo.statsservice.controller;

import com.github.pagehelper.page.PageMethod;
import com.orange.demo.statsservice.model.*;
import com.orange.demo.statsservice.service.*;
import com.orange.demo.statsapi.dto.*;
import com.orange.demo.statsapi.vo.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.IBaseService;
import com.orange.demo.common.core.annotation.MyRequestBody;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学生行为统计操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "学生行为统计管理接口")
@Slf4j
@RestController
@RequestMapping("/studentActionStats")
public class StudentActionStatsController extends BaseController<StudentActionStats, StudentActionStatsVo, Long> {

    @Autowired
    private StudentActionStatsService studentActionStatsService;

    @Override
    protected IBaseService<StudentActionStats, Long> service() {
        return studentActionStatsService;
    }

    /**
     * 列出符合过滤条件的学生行为统计列表。
     *
     * @param studentActionStatsDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<StudentActionStatsVo>> list(
            @MyRequestBody StudentActionStatsDto studentActionStatsDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        StudentActionStats studentActionStatsFilter = MyModelUtil.copyTo(studentActionStatsDtoFilter, StudentActionStats.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, StudentActionStats.class);
        List<StudentActionStats> studentActionStatsList =
                studentActionStatsService.getStudentActionStatsListWithRelation(studentActionStatsFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(studentActionStatsList, StudentActionStats.INSTANCE));
    }

    /**
     * 分组列出符合过滤条件的学生行为统计列表。
     *
     * @param studentActionStatsDtoFilter 过滤对象。
     * @param groupParam 分组参数。
     * @param orderParam 排序参数。
     * @param pageParam  分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/listWithGroup")
    public ResponseResult<MyPageData<StudentActionStatsVo>> listWithGroup(
            @MyRequestBody StudentActionStatsDto studentActionStatsDtoFilter,
            @MyRequestBody(required = true) MyGroupParam groupParam,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        String orderBy = MyOrderParam.buildOrderBy(orderParam, StudentActionStats.class);
        groupParam = MyGroupParam.buildGroupBy(groupParam, StudentActionStats.class);
        if (groupParam == null) {
            return ResponseResult.error(
                    ErrorCodeEnum.INVALID_ARGUMENT_FORMAT, "数据参数错误，分组参数不能为空！");
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        StudentActionStats filter = MyModelUtil.copyTo(studentActionStatsDtoFilter, StudentActionStats.class);
        MyGroupCriteria criteria = groupParam.getGroupCriteria();
        List<StudentActionStats> resultList = studentActionStatsService.getGroupedStudentActionStatsListWithRelation(
                filter, criteria.getGroupSelect(), criteria.getGroupBy(), orderBy);
        // 分页连同对象数据转换copy工作，下面的方法一并完成。
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList, StudentActionStats.INSTANCE));
    }

    /**
     * 查看指定学生行为统计对象详情。
     *
     * @param statsId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<StudentActionStatsVo> view(@RequestParam Long statsId) {
        if (MyCommonUtil.existBlankArgument(statsId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        StudentActionStats studentActionStats = studentActionStatsService.getByIdWithRelation(statsId, MyRelationParam.full());
        if (studentActionStats == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        StudentActionStatsVo studentActionStatsVo = StudentActionStats.INSTANCE.fromModel(studentActionStats);
        return ResponseResult.success(studentActionStatsVo);
    }

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param statsIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    @ApiOperation(hidden = true, value = "listByIds")
    @PostMapping("/listByIds")
    public ResponseResult<List<StudentActionStatsVo>> listByIds(
            @RequestParam Set<Long> statsIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(statsIds, withDict, StudentActionStats.INSTANCE);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param statsId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @ApiOperation(hidden = true, value = "getById")
    @PostMapping("/getById")
    public ResponseResult<StudentActionStatsVo> getById(
            @RequestParam Long statsId, @RequestParam Boolean withDict) {
        return super.baseGetById(statsId, withDict, StudentActionStats.INSTANCE);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param statsIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existIds")
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
    @ApiOperation(hidden = true, value = "existId")
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long statsId) {
        return super.baseExistId(statsId);
    }

    /**
     * 根据主键Id删除数据。
     *
     * @param statsId 主键Id。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteById")
    @PostMapping("/deleteById")
    public ResponseResult<Integer> deleteById(@RequestParam Long statsId) throws Exception {
        StudentActionStats filter = new StudentActionStats();
        filter.setStatsId(statsId);
        return super.baseDeleteBy(filter);
    }

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteBy")
    @PostMapping("/deleteBy")
    public ResponseResult<Integer> deleteBy(@RequestBody StudentActionStatsDto filter) throws Exception {
        return super.baseDeleteBy(MyModelUtil.copyTo(filter, StudentActionStats.class));
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listBy")
    @PostMapping("/listBy")
    public ResponseResult<MyPageData<StudentActionStatsVo>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, StudentActionStats.INSTANCE);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listMapBy")
    @PostMapping("/listMapBy")
    public ResponseResult<MyPageData<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListMapBy(queryParam, StudentActionStats.INSTANCE);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @ApiOperation(hidden = true, value = "getBy")
    @PostMapping("/getBy")
    public ResponseResult<StudentActionStatsVo> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, StudentActionStats.INSTANCE);
    }

    /**
     * 获取远程主对象中符合查询条件的数据数量。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @ApiOperation(hidden = true, value = "countBy")
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
    @ApiOperation(hidden = true, value = "aggregateBy")
    @PostMapping("/aggregateBy")
    public ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam) {
        return super.baseAggregateBy(aggregationParam);
    }
}
