package com.orange.demo.webadmin.upms.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.util.MyPageUtil;
import com.orange.demo.common.log.model.SysOperationLog;
import com.orange.demo.common.log.service.SysOperationLogService;
import com.orange.demo.webadmin.upms.dto.SysOperationLogDto;
import com.orange.demo.webadmin.upms.vo.SysOperationLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志接口控制器对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysOperationLog")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService operationLogService;

    /**
     * 数据权限列表。
     *
     * @param sysOperationLogDtoFilter 操作日志查询过滤对象。
     * @param orderParam               排序参数。
     * @param pageParam                分页参数。
     * @return 应答结果对象。包含操作日志列表。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysOperationLogVo>> list(
            @MyRequestBody SysOperationLogDto sysOperationLogDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysOperationLog filter = MyModelUtil.copyTo(sysOperationLogDtoFilter, SysOperationLog.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysOperationLog.class);
        List<SysOperationLog> operationLogList = operationLogService.getSysOperationLogList(filter, orderBy);
        List<SysOperationLogVo> operationLogVoList = MyModelUtil.copyCollectionTo(operationLogList, SysOperationLogVo.class);
        long totalCount = 0L;
        if (operationLogList instanceof Page) {
            totalCount = ((Page<SysOperationLog>) operationLogList).getTotal();
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(operationLogVoList, totalCount));
    }
}
