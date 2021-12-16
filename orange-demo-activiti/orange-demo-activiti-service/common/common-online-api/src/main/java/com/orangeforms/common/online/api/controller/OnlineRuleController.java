package com.orangeforms.common.online.api.controller;

import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.MyCommonUtil;
import com.orangeforms.common.core.util.MyModelUtil;
import com.orangeforms.common.core.util.MyPageUtil;
import com.orangeforms.common.core.validator.UpdateGroup;
import com.orangeforms.common.online.dto.OnlineRuleDto;
import com.orangeforms.common.online.model.OnlineRule;
import com.orangeforms.common.online.service.OnlineRuleService;
import com.orangeforms.common.online.vo.OnlineRuleVo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 验证规则操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("${common-online-api.urlPrefix}/onlineRule")
public class OnlineRuleController {

    @Autowired
    private OnlineRuleService onlineRuleService;

    /**
     * 新增验证规则数据。
     *
     * @param onlineRuleDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody OnlineRuleDto onlineRuleDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineRuleDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineRule onlineRule = MyModelUtil.copyTo(onlineRuleDto, OnlineRule.class);
        onlineRule = onlineRuleService.saveNew(onlineRule);
        return ResponseResult.success(onlineRule.getRuleId());
    }

    /**
     * 更新验证规则数据。
     *
     * @param onlineRuleDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody OnlineRuleDto onlineRuleDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(onlineRuleDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineRule onlineRule = MyModelUtil.copyTo(onlineRuleDto, OnlineRule.class);
        OnlineRule originalOnlineRule = onlineRuleService.getById(onlineRule.getRuleId());
        if (originalOnlineRule == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前在线字段规则并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineRuleService.update(onlineRule, originalOnlineRule)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除验证规则数据。
     *
     * @param ruleId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long ruleId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(ruleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        OnlineRule originalOnlineRule = onlineRuleService.getById(ruleId);
        if (originalOnlineRule == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前在线字段规则并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!onlineRuleService.remove(ruleId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的验证规则列表。
     *
     * @param onlineRuleDtoFilter 过滤对象。
     * @param orderParam          排序参数。
     * @param pageParam           分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<OnlineRuleVo>> list(
            @MyRequestBody OnlineRuleDto onlineRuleDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        OnlineRule onlineRuleFilter = MyModelUtil.copyTo(onlineRuleDtoFilter, OnlineRule.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, OnlineRule.class);
        List<OnlineRule> onlineRuleList = onlineRuleService.getOnlineRuleListWithRelation(onlineRuleFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(onlineRuleList, OnlineRule.INSTANCE));
    }

    /**
     * 查看指定验证规则对象详情。
     *
     * @param ruleId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<OnlineRuleVo> view(@RequestParam Long ruleId) {
        if (MyCommonUtil.existBlankArgument(ruleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        OnlineRule onlineRule = onlineRuleService.getByIdWithRelation(ruleId, MyRelationParam.full());
        if (onlineRule == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        OnlineRuleVo onlineRuleVo = OnlineRule.INSTANCE.fromModel(onlineRule);
        return ResponseResult.success(onlineRuleVo);
    }
}
