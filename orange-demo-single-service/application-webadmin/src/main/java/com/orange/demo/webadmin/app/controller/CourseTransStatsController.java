package com.orange.demo.webadmin.app.controller;

import com.github.pagehelper.page.PageMethod;
import com.orange.demo.webadmin.app.vo.*;
import com.orange.demo.webadmin.app.dto.*;
import com.orange.demo.webadmin.app.model.*;
import com.orange.demo.webadmin.app.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
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
@Slf4j
@RestController
@RequestMapping("/admin/app/courseTransStats")
public class CourseTransStatsController {

    @Autowired
    private CourseTransStatsService courseTransStatsService;

    /**
     * 列出符合过滤条件的课程统计列表。
     *
     * @param courseTransStatsDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<CourseTransStatsVo>> list(
            @MyRequestBody("courseTransStatsFilter") CourseTransStatsDto courseTransStatsDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        CourseTransStats courseTransStatsFilter = MyModelUtil.copyTo(courseTransStatsDtoFilter, CourseTransStats.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, CourseTransStats.class);
        List<CourseTransStats> courseTransStatsList = courseTransStatsService.getCourseTransStatsListWithRelation(courseTransStatsFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(courseTransStatsList, CourseTransStats.INSTANCE));
    }

    /**
     * 分组列出符合过滤条件的课程统计列表。
     *
     * @param courseTransStatsDtoFilter 过滤对象。
     * @param groupParam 分组参数。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/listWithGroup")
    public ResponseResult<MyPageData<CourseTransStatsVo>> listWithGroup(
            @MyRequestBody("courseTransStatsFilter") CourseTransStatsDto courseTransStatsDtoFilter,
            @MyRequestBody(required = true) MyGroupParam groupParam,
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
        CourseTransStats filter = MyModelUtil.copyTo(courseTransStatsDtoFilter, CourseTransStats.class);
        MyGroupCriteria criteria = groupParam.getGroupCriteria();
        List<CourseTransStats> resultList = courseTransStatsService.getGroupedCourseTransStatsListWithRelation(
                filter, criteria.getGroupSelect(), criteria.getGroupBy(), orderBy);
        // 分页连同对象数据转换copy工作，下面的方法一并完成。
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList, CourseTransStats.INSTANCE));
    }

    /**
     * 查看指定课程统计对象详情。
     *
     * @param statsId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<CourseTransStatsVo> view(@RequestParam Long statsId) {
        if (MyCommonUtil.existBlankArgument(statsId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        CourseTransStats courseTransStats = courseTransStatsService.getByIdWithRelation(statsId, MyRelationParam.full());
        if (courseTransStats == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        CourseTransStatsVo courseTransStatsVo = CourseTransStats.INSTANCE.fromModel(courseTransStats);
        return ResponseResult.success(courseTransStatsVo);
    }
}
