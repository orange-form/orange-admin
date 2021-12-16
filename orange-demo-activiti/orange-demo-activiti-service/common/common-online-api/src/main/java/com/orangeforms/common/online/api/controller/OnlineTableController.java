package com.orangeforms.common.online.api.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.dto.OnlineTableDto;
import com.orangeforms.common.online.model.OnlineTable;
import com.orangeforms.common.online.service.OnlineTableService;
import com.orangeforms.common.online.vo.OnlineTableVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Map;

/**
 * 数据表操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineTable")
public class OnlineTableController {

    @Autowired
    private OnlineTableService onlineTableService;

    /**
     * 更新数据表数据。
     *
     * @param onlineTableDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineTableDto onlineTableDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineTableDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineTable onlineTable = MyModelUtil.copyTo(onlineTableDto, OnlineTable.class);
        OnlineTable originalOnlineTable = onlineTableService.getById(onlineTable.getTableId());
        if (originalOnlineTable == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前在线数据表并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineTableService.update(onlineTable, originalOnlineTable)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的数据表列表。
     *
     * @param onlineTableDtoFilter 过滤对象。
     * @param orderParam           排序参数。
     * @param pageParam            分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineTableVo>> list(
            @MyRequestBody OnlineTableDto onlineTableDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineTable onlineTableFilter = MyModelUtil.copyTo(onlineTableDtoFilter, OnlineTable.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineTable.class);
        List<OnlineTable> onlineTableList =
                onlineTableService.getOnlineTableListWithRelation(onlineTableFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineTableList, OnlineTable.INSTANCE));
    }

    /**
     * 查看指定数据表对象详情。
     *
     * @param tableId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<OnlineTableVo> view(@RequestParam Long tableId) {
        if (MyCommonUtil.existBlankArgument(tableId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineTable onlineTable = onlineTableService.getByIdWithRelation(tableId, MyRelationParam.full());
        if (onlineTable == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineTableVo onlineTableVo = OnlineTable.INSTANCE.fromModel(onlineTable);
        return ResponseResult.success(onlineTableVo);
    }

    /**
     * 以字典形式返回全部数据表数据集合。字典的键值为[tableId, modelName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(OnlineTable filter) {
        List<OnlineTable> resultList = onlineTableService.getListByFilter(filter);
        return ResponseResult.success(BeanQuery.select(
                "tableId as id", "modelName as name").executeFrom(resultList));
    }
}
