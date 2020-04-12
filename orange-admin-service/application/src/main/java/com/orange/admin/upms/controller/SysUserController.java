package com.orange.admin.upms.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.PageHelper;
import com.orange.admin.upms.model.*;
import com.orange.admin.upms.service.*;
import com.orange.admin.common.core.object.*;
import com.orange.admin.common.core.util.*;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.annotation.MyRequestBody;
import com.orange.admin.common.core.validator.UpdateGroup;
import com.orange.admin.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;

/**
 * 用户管理操作控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * 新增用户操作。
     *
     * @param sysUser              新增用户对象。
     * @param roleIdListString     逗号分隔的角色Id列表。
     * @param dataPermIdListString 逗号分隔的数据权限Id列表。
     * @return 应答结果对象，包含新增用户的主键Id。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/add")
    public ResponseResult<?> add(
            @MyRequestBody SysUser sysUser,
            @MyRequestBody String roleIdListString,
            @MyRequestBody String dataPermIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        JSONObject responseData = null;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysUser);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            VerifyResult result = sysUserService.verifyRelatedData(
                    sysUser, null, roleIdListString, dataPermIdListString);
            if (!result.isSuccess()) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                errorMessage = result.getErrorMessage();
                break;
            }
            Set<Long> roleIdSet = (Set<Long>) result.getData().get("roleIdSet");
            Set<Long> dataPermIdSet = (Set<Long>) result.getData().get("dataPermIdSet");
            sysUserService.saveNew(sysUser, roleIdSet, dataPermIdSet, applicationConfig.getPasswordSalt());
            responseData = new JSONObject();
            responseData.put("userId", sysUser.getUserId());
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, responseData);
    }

    /**
     * 更新用户操作。
     *
     * @param sysUser              更新用户对象。
     * @param roleIdListString     逗号分隔的角色Id列表。
     * @param dataPermIdListString 逗号分隔的数据权限Id列表。
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/update")
    public ResponseResult<?> update(
            @MyRequestBody SysUser sysUser,
            @MyRequestBody String roleIdListString,
            @MyRequestBody String dataPermIdListString) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage;
        do {
            errorMessage = MyCommonUtil.getModelValidationError(sysUser, Default.class, UpdateGroup.class);
            if (errorMessage != null) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                break;
            }
            SysUser originalUser = sysUserService.getById(sysUser.getUserId());
            if (originalUser == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
            VerifyResult result = sysUserService.verifyRelatedData(
                    sysUser, originalUser, roleIdListString, dataPermIdListString);
            if (!result.isSuccess()) {
                errorCodeEnum = ErrorCodeEnum.DATA_VALIDATAED_FAILED;
                errorMessage = result.getErrorMessage();
                break;
            }
            Set<Long> roleIdSet = (Set<Long>) result.getData().get("roleIdSet");
            Set<Long> dataPermIdSet = (Set<Long>) result.getData().get("dataPermIdSet");
            if (!sysUserService.update(sysUser, originalUser, roleIdSet, dataPermIdSet)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "更新失败，数据不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 重置密码操作。
     *
     * @param userId 指定用户主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/resetPassword")
    public ResponseResult<?> resetPassword(@MyRequestBody Long userId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(userId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            if (!sysUserService.resetPassword(
                    userId, applicationConfig.getDefaultUserPassword(), applicationConfig.getPasswordSalt())) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 删除用户管理数据。
     *
     * @param userId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<?> delete(@MyRequestBody Long userId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        do {
            if (MyCommonUtil.existBlankArgument(userId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            // 验证关联Id的数据合法性
            SysUser originalSysUser = sysUserService.getById(userId);
            if (originalSysUser == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                //TODO 修改下面方括号中的话述
                errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
                break;
            }
            if (!sysUserService.remove(userId)) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage);
    }

    /**
     * 列出符合过滤条件的用户管理列表。
     *
     * @param sysUserFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<?> list(
            @MyRequestBody SysUser sysUserFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
        List<SysUser> resultList = sysUserService.getSysUserListWithRelation(sysUserFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定用户管理对象详情。
     *
     * @param userId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysUser> view(@RequestParam Long userId) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        SysUser sysUser = null;
        do {
            if (MyCommonUtil.existBlankArgument(userId)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            sysUser = sysUserService.getByIdWithRelation(userId);
            if (sysUser == null) {
                errorCodeEnum = ErrorCodeEnum.DATA_NOT_EXIST;
                break;
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, sysUser);
    }

    /**
     * 以字典形式返回全部用户管理数据集合。字典的键值为[userId, loginName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDictSysUser")
    public ResponseResult<?> listDictSysUser(SysUser filter) {
        List<SysUser> resultList = sysUserService.getListByFilter(filter);
        return ResponseResult.success(BeanQuery.select(
                "userId as id", "loginName as name").executeFrom(resultList));
    }
}
