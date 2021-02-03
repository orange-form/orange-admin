package com.orange.demo.app.controller;

import com.github.pagehelper.page.PageMethod;
import com.orange.demo.app.vo.*;
import com.orange.demo.app.dto.*;
import com.orange.demo.app.model.*;
import com.orange.demo.app.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 学生行为统计操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
@RestController
@RequestMapping("/admin/app/studentActionStats")
public class StudentActionStatsController {

    @Autowired
    private StudentActionStatsService studentActionStatsService;

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
            @MyRequestBody("studentActionStatsFilter") StudentActionStatsDto studentActionStatsDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        StudentActionStats studentActionStatsFilter = MyModelUtil.copyTo(studentActionStatsDtoFilter, StudentActionStats.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, StudentActionStats.class);
        List<StudentActionStats> studentActionStatsList = studentActionStatsService.getStudentActionStatsListWithRelation(studentActionStatsFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(studentActionStatsList, StudentActionStats.INSTANCE));
    }

    /**
     * 分组列出符合过滤条件的学生行为统计列表。
     *
     * @param studentActionStatsDtoFilter 过滤对象。
     * @param groupParam 分组参数。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/listWithGroup")
    public ResponseResult<MyPageData<StudentActionStatsVo>> listWithGroup(
            @MyRequestBody("studentActionStatsFilter") StudentActionStatsDto studentActionStatsDtoFilter,
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
}
