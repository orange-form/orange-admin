package com.orange.demo.upmsservice.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.util.MyPageUtil;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.upmsapi.dto.SysRoleDto;
import com.orange.demo.upmsapi.dto.SysUserDto;
import com.orange.demo.upmsapi.vo.SysRoleVo;
import com.orange.demo.upmsapi.vo.SysUserVo;
import com.orange.demo.upmsservice.model.SysRole;
import com.orange.demo.upmsservice.model.SysUser;
import com.orange.demo.upmsservice.model.SysUserRole;
import com.orange.demo.upmsservice.service.SysRoleService;
import com.orange.demo.upmsservice.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色管理接口控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "角色管理接口")
@Slf4j
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 新增角色操作。
     *
     * @param sysRoleDto       新增角色对象。
     * @param menuIdListString 与当前角色Id绑定的menuId列表，多个menuId之间逗号分隔。
     * @return 应答结果对象，包含新增角色的主键Id。
     */
    @SuppressWarnings("unchecked")
    @ApiOperationSupport(ignoreParameters = {"sysRole.roleId", "sysRole.createTimeStart", "sysRole.createTimeEnd"})
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody("sysRole") SysRoleDto sysRoleDto, @MyRequestBody String menuIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysRoleDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysRole sysRole = MyModelUtil.copyTo(sysRoleDto, SysRole.class);
        CallResult result = sysRoleService.verifyRelatedData(sysRole, null, menuIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> menuIdSet = null;
        if (result.getData() != null) {
            menuIdSet = (Set<Long>) result.getData().get("menuIdSet");
        }
        sysRoleService.saveNew(sysRole, menuIdSet);
        return ResponseResult.success(sysRole.getRoleId());
    }

    /**
     * 更新角色操作。
     *
     * @param sysRoleDto       更新角色对象。
     * @param menuIdListString 与当前角色Id绑定的menuId列表，多个menuId之间逗号分隔。
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @ApiOperationSupport(ignoreParameters = {"sysRole.createTimeStart", "sysRole.createTimeEnd"})
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody("sysRole") SysRoleDto sysRoleDto, @MyRequestBody String menuIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysRoleDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysRole originalSysRole = sysRoleService.getById(sysRoleDto.getRoleId());
        if (originalSysRole == null) {
            errorMessage = "数据验证失败，当前角色并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        SysRole sysRole = MyModelUtil.copyTo(sysRoleDto, SysRole.class);
        CallResult result = sysRoleService.verifyRelatedData(sysRole, originalSysRole, menuIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> menuIdSet = null;
        if (result.getData() != null) {
            menuIdSet = (Set<Long>) result.getData().get("menuIdSet");
        }
        if (!sysRoleService.update(sysRole, originalSysRole, menuIdSet)) {
            errorMessage = "更新失败，数据不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 删除指定角色操作。
     *
     * @param roleId 指定角色主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long roleId) {
        if (MyCommonUtil.existBlankArgument(roleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysRoleService.remove(roleId)) {
            String errorMessage = "数据操作失败，角色不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 查看角色列表。
     *
     * @param sysRoleDtoFilter 角色过滤对象。
     * @param orderParam       排序参数。
     * @param pageParam        分页参数。
     * @return 应答结果对象，包含角色列表。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SysRoleVo>> list(
            @MyRequestBody("sysRoleFilter") SysRoleDto sysRoleDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysRole filter = MyModelUtil.copyTo(sysRoleDtoFilter, SysRole.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysRole.class);
        List<SysRole> roleList = sysRoleService.getSysRoleList(filter, orderBy);
        List<SysRoleVo> roleVoList = MyModelUtil.copyCollectionTo(roleList, SysRoleVo.class);
        long totalCount = 0L;
        if (roleList instanceof Page) {
            totalCount = ((Page<SysRole>) roleList).getTotal();
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(roleVoList, totalCount));
    }

    /**
     * 查看角色详情。
     *
     * @param roleId 指定角色主键Id。
     * @return 应答结果对象，包含角色详情对象。
     */
    @GetMapping("/view")
    public ResponseResult<SysRoleVo> view(@RequestParam Long roleId) {
        if (MyCommonUtil.existBlankArgument(roleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysRole sysRole = sysRoleService.getByIdWithRelation(roleId, MyRelationParam.full());
        if (sysRole == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysRoleVo sysRoleVo = MyModelUtil.copyTo(sysRole, SysRoleVo.class);
        return ResponseResult.success(sysRoleVo);
    }

    /**
     * 获取不包含指定角色Id的用户列表。
     * 用户和角色是多对多关系，当前接口将返回没有赋值指定RoleId的用户列表。可用于给角色添加新用户。
     *
     * @param roleId           角色主键Id。
     * @param sysUserDtoFilter 用户过滤对象。
     * @param orderParam       排序参数。
     * @param pageParam        分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listNotInUserRole")
    public ResponseResult<MyPageData<SysUserVo>> listNotInUserRole(
            @MyRequestBody Long roleId,
            @MyRequestBody("sysUserFilter") SysUserDto sysUserDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doRoleUserVerify(roleId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysUser filter = MyModelUtil.copyTo(sysUserDtoFilter, SysUser.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
        List<SysUser> userList = sysUserService.getNotInSysUserListByRoleId(roleId, filter, orderBy);
        List<SysUserVo> userVoList = MyModelUtil.copyCollectionTo(userList, SysUserVo.class);
        return ResponseResult.success(MyPageUtil.makeResponseData(userVoList));
    }

    /**
     * 拥有指定角色的用户列表。
     *
     * @param roleId           角色主键Id。
     * @param sysUserDtoFilter 用户过滤对象。
     * @param orderParam       排序参数。
     * @param pageParam        分页参数。
     * @return 应答结果对象，包含用户列表数据。
     */
    @PostMapping("/listUserRole")
    public ResponseResult<MyPageData<SysUserVo>> listUserRole(
            @MyRequestBody Long roleId,
            @MyRequestBody("sysUserFilter") SysUserDto sysUserDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        ResponseResult<Void> verifyResult = this.doRoleUserVerify(roleId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysUser filter = MyModelUtil.copyTo(sysUserDtoFilter, SysUser.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
        List<SysUser> userList = sysUserService.getSysUserListByRoleId(roleId, filter, orderBy);
        List<SysUserVo> userVoList = MyModelUtil.copyCollectionTo(userList, SysUserVo.class);
        return ResponseResult.success(MyPageUtil.makeResponseData(userVoList));
    }

    private ResponseResult<Void> doRoleUserVerify(Long roleId) {
        if (MyCommonUtil.existBlankArgument(roleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysRoleService.existId(roleId)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        return ResponseResult.success();
    }

    /**
     * 为指定角色添加用户列表。该操作可同时给一批用户赋值角色，并在同一事务内完成。
     *
     * @param roleId           角色主键Id。
     * @param userIdListString 逗号分隔的用户Id列表。
     * @return 应答结果对象。
     */
    @PostMapping("/addUserRole")
    public ResponseResult<Void> addUserRole(
            @MyRequestBody Long roleId, @MyRequestBody String userIdListString) {
        if (MyCommonUtil.existBlankArgument(roleId, userIdListString)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        Set<Long> userIdSet = Arrays.stream(
                userIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysRoleService.existId(roleId)
                || !sysUserService.existUniqueKeyList("userId", userIdSet)) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_RELATED_RECORD_ID);
        }
        List<SysUserRole> userRoleList = new LinkedList<>();
        for (Long userId : userIdSet) {
            SysUserRole userRole = new SysUserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            userRoleList.add(userRole);
        }
        sysRoleService.addUserRoleList(userRoleList);
        return ResponseResult.success();
    }

    /**
     * 为指定用户移除指定角色。
     *
     * @param roleId 指定角色主键Id。
     * @param userId 指定用户主键Id。
     * @return 应答数据结果。
     */
    @PostMapping("/deleteUserRole")
    public ResponseResult<Void> deleteUserRole(
            @MyRequestBody Long roleId, @MyRequestBody Long userId) {
        if (MyCommonUtil.existBlankArgument(roleId, userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!sysRoleService.removeUserRole(roleId, userId)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 查询角色的权限资源地址列表。同时返回详细的分配路径。
     *
     * @param roleId 角色Id。
     * @param url    url过滤条件。
     * @return 应答对象，包含从角色到权限资源的完整权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysPermWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysPermWithDetail(Long roleId, String url) {
        if (MyCommonUtil.isBlankOrNull(roleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysRoleService.getSysPermListWithDetail(roleId, url));
    }

    /**
     * 查询角色的权限字列表。同时返回详细的分配路径。
     *
     * @param roleId   角色Id。
     * @param permCode 权限字名称过滤条件。
     * @return 应答对象，包含从角色到权限字的权限分配路径信息的查询结果列表。
     */
    @GetMapping("/listSysPermCodeWithDetail")
    public ResponseResult<List<Map<String, Object>>> listSysPermCodeWithDetail(Long roleId, String permCode) {
        if (MyCommonUtil.isBlankOrNull(roleId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        return ResponseResult.success(sysRoleService.getSysPermCodeListWithDetail(roleId, permCode));
    }
}
