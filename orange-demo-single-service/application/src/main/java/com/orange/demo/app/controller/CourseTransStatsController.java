package com.orange.demo.app.controller;

import com.github.pagehelper.page.PageMethod;
import com.orange.demo.app.model.*;
import com.orange.demo.app.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 课程统计操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Api(tags = "课程统计管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/courseTransStats")
public class CourseTransStatsController {

    @Autowired
    private CourseTransStatsService courseTransStatsService;

    /**
     * 列出符合过滤条件的课程统计列表。
     *
     * @param courseTransStatsFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<CourseTransStats>> list(
            @MyRequestBody CourseTransStats courseTransStatsFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, CourseTransStats.class);
        List<CourseTransStats> resultList = courseTransStatsService.getCourseTransStatsListWithRelation(courseTransStatsFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 分组列出符合过滤条件的课程统计列表。
     *
     * @param courseTransStatsFilter 过滤对象。
     * @param groupParam 分组参数。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/listWithGroup")
    public ResponseResult<MyPageData<CourseTransStats>> listWithGroup(
            @MyRequestBody CourseTransStats courseTransStatsFilter,
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
        MyGroupCriteria criteria = groupParam.getGroupCriteria();
        List<CourseTransStats> resultList = courseTransStatsService.getGroupedCourseTransStatsListWithRelation(
                courseTransStatsFilter, criteria.getGroupSelect(), criteria.getGroupBy(), orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定课程统计对象详情。
     *
     * @param statsId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<CourseTransStats> view(@RequestParam Long statsId) {
        if (MyCommonUtil.existBlankArgument(statsId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        CourseTransStats courseTransStats = courseTransStatsService.getByIdWithRelation(statsId, MyRelationParam.full());
        if (courseTransStats == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(courseTransStats);
    }
}
