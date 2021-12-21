package com.orangeforms.uaaadmin.controller;

import com.orangeforms.uaaadmin.config.ApplicationConfig;
import com.orangeforms.uaaadmin.model.SysUaaUser;
import com.orangeforms.uaaadmin.service.SysUaaUserService;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.validator.AddGroup;
import com.orangeforms.common.core.validator.UpdateGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.groups.Default;

/**
 * UAA用户操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/admin/uaaadmin/sysUaaUser")
public class SysUaaUserController {

    @Autowired
    private SysUaaUserService sysUaaUserService;
    @Autowired
    private ApplicationConfig appConfig;

    /**
     * 新增UAA用户数据。
     *
     * @param sysUaaUser 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SysUaaUser sysUaaUser) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUaaUser, Default.class, AddGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        sysUaaUser = sysUaaUserService.saveNew(sysUaaUser);
        return ResponseResult.success(sysUaaUser.getUserId());
    }

    /**
     * 更新UAA用户数据。
     *
     * @param sysUaaUser 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SysUaaUser sysUaaUser) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUaaUser, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        SysUaaUser originalSysUaaUser = sysUaaUserService.getById(sysUaaUser.getUserId());
        if (originalSysUaaUser == null) {
            errorMessage = "数据验证失败，当前UAA用户并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!sysUaaUserService.update(sysUaaUser, originalSysUaaUser)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 重置UAA用户密码到初始化密码。
     *
     * @param uaaUserId 用户Id。
     * @return 应答结果对象。
     */
    @PostMapping("/resetPassword")
    public ResponseResult<Void> resetPassword(@MyRequestBody Long uaaUserId) {
        if (MyCommonUtil.existBlankArgument(uaaUserId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysUaaUserService.changePassword(uaaUserId, appConfig.getDefaultUserPassword())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除UAA用户数据。
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
        SysUaaUser originalSysUaaUser = sysUaaUserService.getById(userId);
        if (originalSysUaaUser == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!sysUaaUserService.remove(userId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的UAA用户列表。
     *
     * @param sysUaaUserFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysUaaUser>> list(
            @MyRequestBody SysUaaUser sysUaaUserFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUaaUser.class);
        List<SysUaaUser> resultList = sysUaaUserService.getSysUaaUserListWithRelation(sysUaaUserFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定UAA用户对象详情。
     *
     * @param userId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysUaaUser> view(@RequestParam Long userId) {
        if (MyCommonUtil.existBlankArgument(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysUaaUser sysUaaUser = sysUaaUserService.getByIdWithRelation(userId, MyRelationParam.full());
        if (sysUaaUser == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(sysUaaUser);
    }
}
