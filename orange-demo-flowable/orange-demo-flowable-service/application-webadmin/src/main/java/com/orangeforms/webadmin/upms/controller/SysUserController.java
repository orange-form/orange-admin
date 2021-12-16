package com.orangeforms.webadmin.upms.controller;

import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.webadmin.upms.vo.*;
import com.orangeforms.webadmin.upms.dto.*;
import com.orangeforms.webadmin.upms.model.*;
import com.orangeforms.webadmin.upms.service.*;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.*;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.webadmin.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户管理操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
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
     * @param sysUserDto           新增用户对象。
     * @param deptPostIdListString 逗号分隔的部门岗位Id列表。
     * @param dataPermIdListString 逗号分隔的数据权限Id列表。
     * @param roleIdListString     逗号分隔的角色Id列表。
     * @return 应答结果对象，包含新增用户的主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody SysUserDto sysUserDto,
            @MyRequestBody String deptPostIdListString,
            @MyRequestBody String dataPermIdListString,
            @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUserDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysUser sysUser = MyModelUtil.copyTo(sysUserDto, SysUser.class);
        CallResult result = sysUserService.verifyRelatedData(
                sysUser, null, roleIdListString, deptPostIdListString, dataPermIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> deptPostIdSet = result.getData().getObject("deptPostIdSet", new TypeReference<Set<Long>>() {});
        Set<Long> roleIdSet = result.getData().getObject("roleIdSet", new TypeReference<Set<Long>>() {});
        Set<Long> dataPermIdSet = result.getData().getObject("dataPermIdSet", new TypeReference<Set<Long>>() {});
        sysUserService.saveNew(sysUser, roleIdSet, deptPostIdSet, dataPermIdSet);
        return ResponseResult.success(sysUser.getUserId());
    }

    /**
     * 更新用户操作。
     *
     * @param sysUserDto           更新用户对象。
     * @param deptPostIdListString 逗号分隔的部门岗位Id列表。
     * @param dataPermIdListString 逗号分隔的数据权限Id列表。
     * @param roleIdListString     逗号分隔的角色Id列表。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody SysUserDto sysUserDto,
            @MyRequestBody String deptPostIdListString,
            @MyRequestBody String dataPermIdListString,
            @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUserDto, true);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysUser originalUser = sysUserService.getById(sysUserDto.getUserId());
        if (originalUser == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysUser sysUser = MyModelUtil.copyTo(sysUserDto, SysUser.class);
        CallResult result = sysUserService.verifyRelatedData(
                sysUser, originalUser, roleIdListString, deptPostIdListString, dataPermIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> deptPostIdSet = result.getData().getObject("deptPostIdSet", new TypeReference<Set<Long>>() {});
        Set<Long> roleIdSet = result.getData().getObject("roleIdSet", new TypeReference<Set<Long>>() {});
        Set<Long> dataPermIdSet = result.getData().getObject("dataPermIdSet", new TypeReference<Set<Long>>() {});
        if (!sysUserService.update(sysUser, originalUser, roleIdSet, deptPostIdSet, dataPermIdSet)) {
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
     * @param sysUserDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysUserVo>> list(
            @MyRequestBody SysUserDto sysUserDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysUser sysUserFilter = MyModelUtil.copyTo(sysUserDtoFilter, SysUser.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
        List<SysUser> sysUserList = sysUserService.getSysUserListWithRelation(sysUserFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(sysUserList, SysUser.INSTANCE));
    }

    /**
     * 查看指定用户管理对象详情。
     *
     * @param userId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysUserVo> view(@RequestParam Long userId) {
        if (MyCommonUtil.existBlankArgument(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 这里查看用户数据时候，需要把用户多对多关联的角色和数据权限Id一并查出。
        SysUser sysUser = sysUserService.getByIdWithRelation(userId, MyRelationParam.full());
        if (sysUser == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysUserVo sysUserVo = SysUser.INSTANCE.fromModel(sysUser);
        return ResponseResult.success(sysUserVo);
    }

    /**
     * 查询用户的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param userId 用户Id。
     * @param url    url过滤条件。
     * @return 应答对象，包含从用户到权限资源的完整权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysPermWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysPermWithDetail(Long userId, String url) {
        if (MyCommonUtil.isBlankOrNull(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysUserService.getSysPermListWithDetail(userId, url));
    }

    /**
     * 查询用户的权限字列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param permCode 权限字名称过滤条件。
     * @return 应答对象，包含从用户到权限字的权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysPermCodeWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysPermCodeWithDetail(Long userId, String permCode) {
        if (MyCommonUtil.isBlankOrNull(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysUserService.getSysPermCodeListWithDetail(userId, permCode));
    }

    /**
     * 查询用户的菜单列表。同时返回详细的分配路径。
     *
     * @param userId   用户Id。
     * @param menuName 菜单名称过滤条件。
     * @return 应答对象，包含从用户到菜单的权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysMenuWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysMenuWithDetail(Long userId, String menuName) {
        if (MyCommonUtil.isBlankOrNull(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysUserService.getSysMenuListWithDetail(userId, menuName));
    }
}
