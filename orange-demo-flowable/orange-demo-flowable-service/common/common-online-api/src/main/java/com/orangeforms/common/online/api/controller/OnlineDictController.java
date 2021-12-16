package com.orangeforms.common.online.api.controller;

import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.dto.OnlineDictDto;
import com.orangeforms.common.online.model.OnlineDict;
import com.orangeforms.common.online.service.OnlineDictService;
import com.orangeforms.common.online.vo.OnlineDictVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 在线表单字典操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineDict")
public class OnlineDictController {

    @Autowired
    private OnlineDictService onlineDictService;

    /**
     * 新增在线表单字典数据。
     *
     * @param onlineDictDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody OnlineDictDto onlineDictDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineDictDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineDict onlineDict = MyModelUtil.copyTo(onlineDictDto, OnlineDict.class);
        // 验证关联Id的数据合法性
        CallResult callResult = onlineDictService.verifyRelatedData(onlineDict, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        onlineDict = onlineDictService.saveNew(onlineDict);
        return ResponseResult.success(onlineDict.getDictId());
    }

    /**
     * 更新在线表单字典数据。
     *
     * @param onlineDictDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineDictDto onlineDictDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineDictDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineDict onlineDict = MyModelUtil.copyTo(onlineDictDto, OnlineDict.class);
        OnlineDict originalOnlineDict = onlineDictService.getById(onlineDict.getDictId());
        if (originalOnlineDict == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前在线字典并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = onlineDictService.verifyRelatedData(onlineDict, originalOnlineDict);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!onlineDictService.update(onlineDict, originalOnlineDict)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除在线表单字典数据。
     *
     * @param dictId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long dictId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(dictId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        OnlineDict originalOnlineDict = onlineDictService.getById(dictId);
        if (originalOnlineDict == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前在线字典并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineDictService.remove(dictId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的在线表单字典列表。
     *
     * @param onlineDictDtoFilter 过滤对象。
     * @param orderParam          排序参数。
     * @param pageParam           分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineDictVo>> list(
            @MyRequestBody OnlineDictDto onlineDictDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineDict onlineDictFilter = MyModelUtil.copyTo(onlineDictDtoFilter, OnlineDict.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineDict.class);
        List<OnlineDict> onlineDictList = onlineDictService.getOnlineDictListWithRelation(onlineDictFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineDictList, OnlineDict.INSTANCE));
    }

    /**
     * 查看指定在线表单字典对象详情。
     *
     * @param dictId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<OnlineDictVo> view(@RequestParam Long dictId) {
        if (MyCommonUtil.existBlankArgument(dictId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineDict onlineDict = onlineDictService.getByIdWithRelation(dictId, MyRelationParam.full());
        if (onlineDict == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineDictVo onlineDictVo = OnlineDict.INSTANCE.fromModel(onlineDict);
        return ResponseResult.success(onlineDictVo);
    }
}
