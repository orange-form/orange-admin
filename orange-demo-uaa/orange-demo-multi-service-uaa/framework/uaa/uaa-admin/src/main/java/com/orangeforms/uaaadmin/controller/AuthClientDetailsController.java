package com.orangeforms.uaaadmin.controller;

import com.orangeforms.uaaadmin.model.AuthClientDetails;
import com.orangeforms.uaaadmin.service.AuthClientDetailsService;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.validator.AddGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;

/**
 * 应用客户端操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/admin/uaaadmin/authClientDetails")
public class AuthClientDetailsController {

    @Autowired
    private AuthClientDetailsService authClientDetailsService;

    /**
     * 新增应用客户端数据。
     *
     * @param authClientDetails 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<String> add(@MyRequestBody AuthClientDetails authClientDetails) {
        String errorMessage = MyCommonUtil.getModelValidationError(authClientDetails, Default.class, AddGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        authClientDetails = authClientDetailsService.saveNew(authClientDetails);
        return ResponseResult.success(authClientDetails.getClientId());
    }

    /**
     * 更新应用客户端数据。
     *
     * @param authClientDetails 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody AuthClientDetails authClientDetails) {
        String errorMessage = MyCommonUtil.getModelValidationError(authClientDetails);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 判断数据合法性。
        AuthClientDetails originalAuthClientDetails = authClientDetailsService.getById(authClientDetails.getClientId());
        if (originalAuthClientDetails == null) {
            errorMessage = "数据验证失败，当前应用客户端并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!originalAuthClientDetails.getClientSecretPlain().equals(authClientDetails.getClientSecretPlain())) {
            errorMessage = "数据验证失败，client_secret不能被修改！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!authClientDetailsService.update(authClientDetails, originalAuthClientDetails)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除应用客户端数据。
     *
     * @param clientId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody String clientId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(clientId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        AuthClientDetails originalAuthClientDetails = authClientDetailsService.getById(clientId);
        if (originalAuthClientDetails == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!authClientDetailsService.remove(clientId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的应用客户端列表。
     *
     * @param authClientDetailsFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<AuthClientDetails>> list(
            @MyRequestBody AuthClientDetails authClientDetailsFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, AuthClientDetails.class);
        List<AuthClientDetails> resultList =
                authClientDetailsService.getAuthClientDetailsListWithRelation(authClientDetailsFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定应用客户端对象详情。
     *
     * @param clientId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<AuthClientDetails> view(@RequestParam String clientId) {
        if (MyCommonUtil.existBlankArgument(clientId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        AuthClientDetails authClientDetails = authClientDetailsService.getByIdWithRelation(clientId, MyRelationParam.full());
        if (authClientDetails == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(authClientDetails);
    }
}
