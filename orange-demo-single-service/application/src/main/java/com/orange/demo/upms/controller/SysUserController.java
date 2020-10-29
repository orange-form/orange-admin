package com.orange.demo.upms.controller;

import com.github.pagehelper.page.PageMethod;
import com.orange.demo.upms.model.*;
import com.orange.demo.upms.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.AddGroup;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.config.ApplicationConfig;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;

/**
 * 用户管理操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Api(tags = "用户管理管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/upms/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationConfig appConfig;

    /**
     * 新增用户操作。
     *
     * @param sysUser          新增用户对象。
     * @param roleIdListString 逗号分隔的角色Id列表。
     * @return 应答结果对象，包含新增用户的主键Id。
     */
    @SuppressWarnings("unchecked")
    @ApiOperationSupport(ignoreParameters = {
            "sysUser.userId",
            "sysUser.createTimeStart",
            "sysUser.createTimeEnd"})
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysUser sysUser, @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUser, Default.class, AddGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        CallResult result = sysUserService.verifyRelatedData(sysUser, null, roleIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        Set<Long> roleIdSet = (Set<Long>) result.getData().get("roleIdSet");
        sysUserService.saveNew(sysUser, roleIdSet);
        return ResponseResult.success(sysUser.getUserId());
    }

    /**
     * 更新用户操作。
     *
     * @param sysUser          更新用户对象。
     * @param roleIdListString 逗号分隔的角色Id列表。
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @ApiOperationSupport(ignoreParameters = {
            "sysUser.createTimeStart",
            "sysUser.createTimeEnd"})
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SysUser sysUser, @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUser, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysUser originalUser = sysUserService.getById(sysUser.getUserId());
        if (originalUser == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        CallResult result = sysUserService.verifyRelatedData(sysUser, originalUser, roleIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, result.getErrorMessage());
        }
        Set<Long> roleIdSet = (Set<Long>) result.getData().get("roleIdSet");
        if (!sysUserService.update(sysUser, originalUser, roleIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 重置密码操作。
     *
     * @param userId 指定用户主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/resetPassword")
    public ResponseResult<Void> resetPassword(@MyRequestBody Long userId) {
        if (MyCommonUtil.existBlankArgument(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysUserService.changePassword(userId, appConfig.getDefaultUserPassword())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除用户管理数据。
     *
     * @param userId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long userId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        SysUser originalSysUser = sysUserService.getById(userId);
        if (originalSysUser == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!sysUserService.remove(userId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
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
    public ResponseResult<MyPageData<SysUser>> list(
            @MyRequestBody SysUser sysUserFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
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
        if (MyCommonUtil.existBlankArgument(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 这里查看用户数据时候，需要把用户多对多关联的角色和数据权限Id一并查出。
        SysUser sysUser = sysUserService.getByIdWithRelation(userId, MyRelationParam.full());
        if (sysUser == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(sysUser);
    }
}
