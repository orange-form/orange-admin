package com.orange.admin.upms.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import com.orange.admin.upms.model.SysPermCode;
import com.orange.admin.upms.service.SysPermCodeService;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.common.core.object.MyPageParam;
import com.orange.admin.common.core.util.MyCommonUtil;
import com.orange.admin.common.core.util.MyPageUtil;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.common.core.annotation.MyRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 权限字管理接口控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysPermCode")
public class SysPermCodeController {

    @Autowired
    private SysPermCodeService sysPermCodeService;

    /**
     * 新增权限字操作。
     *
     * @param sysPermCode      新增权限字对象。
     * @param permIdListString 与当前权限Id绑定的权限资源Id列表，多个权限资源之间逗号分隔。
     * @return 应答结果对象，包含新增权限字的主键Id。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/add")
    public ResponseResult<?> add(@MyRequestBody SysPermCode sysPermCode, @MyRequestBody String permIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        JSONObject responseData = null;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysPermCode);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            VerifyResult result = sysPermCodeService.verifyRelatedData(sysPermCode, null, permIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            Set<Long> permIdSet = null;
            if (result.getData() != null) {
                permIdSet = (Set<Long>) result.getData().get("permIdSet");
            }
            sysPermCode = sysPermCodeService.saveNew(sysPermCode, permIdSet);
            responseData = new JSONObject();
            responseData.put("sysPermCodeId", sysPermCode.getPermCodeId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新权限字操作。
     *
     * @param sysPermCode      更新权限字对象。
     * @param permIdListString 与当前权限Id绑定的权限资源Id列表，多个权限资源之间逗号分隔。
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/update")
    public ResponseResult<?> update(@MyRequestBody SysPermCode sysPermCode, @MyRequestBody String permIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysPermCode, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            SysPermCode originalSysPermCode = sysPermCodeService.getById(sysPermCode.getPermCodeId());
            if (originalSysPermCode == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据验证失败，当前权限字并不存在，请刷新后重试！";
                break;
            }
            VerifyResult result = sysPermCodeService.verifyRelatedData(sysPermCode, originalSysPermCode, permIdListString);
            if (!result.isSuccess()) {
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
            }
            Set<Long> permIdSet = null;
            if (result.getData() != null) {
                permIdSet = (Set<Long>) result.getData().get("permIdSet");
            }
            try {
                if (!sysPermCodeService.update(sysPermCode, originalSysPermCode, permIdSet)) {
                    errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                    errorMessage = "数据验证失败，当前权限字并不存在，请刷新后重试！";
                }
            } catch (DuplicateKeyException e) {
                errorCodeEnum = ErrorCodeEnum.DUPLICATED_UNIQUE_KEY;
                errorMessage = "数据操作失败，权限字编码已经存在！";
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除指定权限字操作。
     *
     * @param permCodeId 指定的权限字主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long permCodeId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(permCodeId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (sysPermCodeService.hasChildren(permCodeId)) {
                errorCodeEnum = ErrorCodeEnum.HAS_CHILDREN_DATA;
                errorMessage = "数据验证失败，当前权限字存在下级权限字！";
                break;
            }
            if (!sysPermCodeService.remove(permCodeId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，权限字不存在，请刷新后重试！";
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 查看权限字列表。
     *
     * @return 应答结果对象，包含权限字列表。
     */
    @PostMapping("/list")
    public ResponseResult<?> list() {
        return ResponseResult.success(sysPermCodeService.getAllListByOrder("permCodeType", "showOrder"));
    }

    /**
     * 查看权限字对象详情。
     *
     * @param permCodeId 指定权限字主键Id。
     * @return 应答结果对象，包含权限字对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysPermCode> view(@RequestParam Long permCodeId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        SysPermCode sysPermCode = null;
        do {
            if (MyCommonUtil.existBlankArgument(permCodeId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            sysPermCode = sysPermCodeService.getSysPermCodeWithRelation(permCodeId);
            if (sysPermCode == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, sysPermCode);
    }

    /**
     * 查看用户关联的权限字列表。
     *
     * @param loginName 精确匹配用户登录名。
     * @param permCode  模糊匹配的权限字名，LIKE %permCode%。
     * @param pageParam 分页对象。
     * @return 应答结果对象，包含该用户的全部权限资源列表。
     */
    @PostMapping("/listAllPermCodesByUserFilter")
    public ResponseResult<?> listAllPermCodesByUserFilter(
            @MyRequestBody String loginName,
            @MyRequestBody String permCode,
            @MyRequestBody MyPageParam pageParam) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject responseData = null;
        do {
            if (MyCommonUtil.existBlankArgument(loginName)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (pageParam != null) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            List<SysPermCode> permCodeList =
                    sysPermCodeService.getUserPermCodeListByFilter(loginName, permCode);
            responseData = MyPageUtil.makeResponseData(permCodeList);
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }
}
