package com.orangeforms.common.online.api.controller;

import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.MyOrderParam;
import com.orangeforms.common.core.object.MyPageData;
import com.orangeforms.common.core.object.MyPageParam;
import com.orangeforms.common.core.object.ResponseResult;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.online.dto.OnlineDblinkDto;
import com.orangeforms.common.online.model.OnlineDblink;
import com.orangeforms.common.online.object.SqlTable;
import com.orangeforms.common.online.object.SqlTableColumn;
import com.orangeforms.common.online.service.OnlineDblinkService;
import com.orangeforms.common.online.vo.OnlineDblinkVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库链接操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineDblink")
public class OnlineDblinkController {

    @Autowired
    private OnlineDblinkService onlineDblinkService;

    /**
     * 列出符合过滤条件的数据库链接列表。
     *
     * @param onlineDblinkDtoFilter 过滤对象。
     * @param orderParam            排序参数。
     * @param pageParam             分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineDblinkVo>> list(
            @MyRequestBody OnlineDblinkDto onlineDblinkDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineDblink onlineDblinkFilter = MyModelUtil.copyTo(onlineDblinkDtoFilter, OnlineDblink.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineDblink.class);
        List<OnlineDblink> onlineDblinkList =
                onlineDblinkService.getOnlineDblinkListWithRelation(onlineDblinkFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineDblinkList, OnlineDblink.INSTANCE));
    }

    /**
     * 获取指定数据库链接下的所有动态表单依赖的数据表列表。
     *
     * @param dblinkId 数据库链接Id。
     * @return 所有动态表单依赖的数据表列表
     */
    @GetMapping("/listDblinkTables")
    public ResponseResult<List<SqlTable>> listDblinkTables(@RequestParam Long dblinkId) {
        OnlineDblink dblink = onlineDblinkService.getById(dblinkId);
        if (dblink == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(onlineDblinkService.getDblinkTableList(dblink));
    }

    /**
     * 获取指定数据库链接下，指定数据表的所有字段信息。
     *
     * @param dblinkId  数据库链接Id。
     * @param tableName 表名。
     * @return 该表的所有字段列表。
     */
    @GetMapping("/listDblinkTableColumns")
    public ResponseResult<List<SqlTableColumn>> listDblinkTableColumns(
            @RequestParam Long dblinkId, @RequestParam String tableName) {
        OnlineDblink dblink = onlineDblinkService.getById(dblinkId);
        if (dblink == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(onlineDblinkService.getDblinkTableColumnList(dblink, tableName));
    }
}
