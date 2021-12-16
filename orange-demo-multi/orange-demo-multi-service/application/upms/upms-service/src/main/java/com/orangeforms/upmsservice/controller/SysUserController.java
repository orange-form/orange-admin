package com.orangeforms.upmsservice.controller;

import com.alibaba.fastjson.TypeReference;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.page.PageMethod;
import com.orangeforms.upmsservice.model.*;
import com.orangeforms.upmsservice.service.*;
import com.orangeforms.upmsapi.dto.*;
import com.orangeforms.upmsapi.vo.*;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.*;
import com.orangeforms.common.core.constant.*;
import com.orangeforms.common.core.base.controller.BaseController;
import com.orangeforms.common.core.base.service.IBaseService;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.log.annotation.OperationLog;
import com.orangeforms.common.log.model.constant.SysOperationLogType;
import com.orangeforms.upmsservice.config.ApplicationConfig;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户管理操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "用户管理管理接口")
@Slf4j
@RestController
@RequestMapping("/sysUser")
public class SysUserController extends BaseController<SysUser, SysUserVo, Long> {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ApplicationConfig appConfig;

    @Override
    protected IBaseService<SysUser, Long> service() {
        return sysUserService;
    }

    /**
     * 新增用户操作。
     *
     * @param sysUserDto           新增用户对象。
     * @param dataPermIdListString 逗号分隔的数据权限Id列表。
     * @param roleIdListString     逗号分隔的角色Id列表。
     * @return 应答结果对象，包含新增用户的主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {
            "sysUserDto.userId",
            "sysUserDto.createTimeStart",
            "sysUserDto.createTimeEnd"})
    @OperationLog(type = SysOperationLogType.ADD)
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody SysUserDto sysUserDto,
            @MyRequestBody String dataPermIdListString,
            @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUserDto, false);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SysUser sysUser = MyModelUtil.copyTo(sysUserDto, SysUser.class);
        CallResult result = sysUserService.verifyRelatedData(
                sysUser, null, roleIdListString, dataPermIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> roleIdSet = result.getData().getObject("roleIdSet", new TypeReference<Set<Long>>() {});
        Set<Long> dataPermIdSet = result.getData().getObject("dataPermIdSet", new TypeReference<Set<Long>>() {});
        sysUserService.saveNew(sysUser, roleIdSet, dataPermIdSet);
        return ResponseResult.success(sysUser.getUserId());
    }

    /**
     * 更新用户操作。
     *
     * @param sysUserDto           更新用户对象。
     * @param dataPermIdListString 逗号分隔的数据权限Id列表。
     * @param roleIdListString     逗号分隔的角色Id列表。
     * @return 应答结果对象。
     */
    @ApiOperationSupport(ignoreParameters = {
            "sysUserDto.createTimeStart",
            "sysUserDto.createTimeEnd"})
    @OperationLog(type = SysOperationLogType.UPDATE)
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody SysUserDto sysUserDto,
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
                sysUser, originalUser, roleIdListString, dataPermIdListString);
        if (!result.isSuccess()) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, result.getErrorMessage());
        }
        Set<Long> roleIdSet = result.getData().getObject("roleIdSet", new TypeReference<Set<Long>>() {});
        Set<Long> dataPermIdSet = result.getData().getObject("dataPermIdSet", new TypeReference<Set<Long>>() {});
        if (!sysUserService.update(sysUser, originalUser, roleIdSet, dataPermIdSet)) {
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
    @OperationLog(type = SysOperationLogType.DELETE)
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

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param userIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    @ApiOperation(hidden = true, value = "listByIds")
    @PostMapping("/listByIds")
    public ResponseResult<List<SysUserVo>> listByIds(
            @RequestParam Set<Long> userIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(userIds, withDict, SysUser.INSTANCE);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param userId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @ApiOperation(hidden = true, value = "getById")
    @PostMapping("/getById")
    public ResponseResult<SysUserVo> getById(
            @RequestParam Long userId, @RequestParam Boolean withDict) {
        return super.baseGetById(userId, withDict, SysUser.INSTANCE);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param userIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existIds")
    @PostMapping("/existIds")
    public ResponseResult<Boolean> existIds(@RequestParam Set<Long> userIds) {
        return super.baseExistIds(userIds);
    }

    /**
     * 判断参数列表中指定的主键Id是否存在。仅限于微服务间远程接口调用。
     *
     * @param userId 主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existId")
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long userId) {
        return super.baseExistId(userId);
    }

    /**
     * 根据最新对象列表和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param data 数据对象。
     *             主键有值是视为更新操作的数据比对，因此仅当关联Id变化时才会验证。
     *             主键为空视为新增操作的数据比对，所有关联Id都会被验证。
     * @return 应答结果对象。
     */
    @ApiOperation(hidden = true, value = "verifyRelatedData")
    @PostMapping("/verifyRelatedData")
    public ResponseResult<Void> verifyRelatedData(@RequestBody SysUserDto data) {
        SysUser sysUser = MyModelUtil.copyTo(data, SysUser.class);
        return super.baseVerifyRelatedData(sysUser, SysUser::getUserId);
    }

    /**
     * 根据最新对象列表和原有对象列表的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param dataList 数据对象列表。
     * @return 应答结果对象。
     */
    @ApiOperation(hidden = true, value = "verifyRelatedDataList")
    @PostMapping("/verifyRelatedDataList")
    public ResponseResult<Void> verifyRelatedDataList(@RequestBody List<SysUserDto> dataList) {
        List<SysUser> sysUserList = MyModelUtil.copyCollectionTo(dataList, SysUser.class);
        return super.baseVerifyRelatedDataList(sysUserList, SysUser::getUserId);
    }

    /**
     * 根据主键Id删除数据。
     *
     * @param userId 主键Id。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteById")
    @PostMapping("/deleteById")
    public ResponseResult<Integer> deleteById(@RequestParam Long userId) throws Exception {
        SysUser filter = new SysUser();
        filter.setUserId(userId);
        return super.baseDeleteBy(filter);
    }

    /**
     * 删除符合过滤条件的数据。
     *
     * @param filter 过滤对象。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteBy")
    @PostMapping("/deleteBy")
    public ResponseResult<Integer> deleteBy(@RequestBody SysUserDto filter) throws Exception {
        return super.baseDeleteBy(MyModelUtil.copyTo(filter, SysUser.class));
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listBy")
    @PostMapping("/listBy")
    public ResponseResult<MyPageData<SysUserVo>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, SysUser.INSTANCE);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listMapBy")
    @PostMapping("/listMapBy")
    public ResponseResult<MyPageData<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListMapBy(queryParam, SysUser.INSTANCE);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @ApiOperation(hidden = true, value = "getBy")
    @PostMapping("/getBy")
    public ResponseResult<SysUserVo> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, SysUser.INSTANCE);
    }

    /**
     * 获取远程主对象中符合查询条件的数据数量。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含结果数量。
     */
    @ApiOperation(hidden = true, value = "countBy")
    @PostMapping("/countBy")
    public ResponseResult<Integer> countBy(@RequestBody MyQueryParam queryParam) {
        return super.baseCountBy(queryParam);
    }

    /**
     * 获取远程对象中符合查询条件的分组聚合计算Map列表。
     *
     * @param aggregationParam 聚合参数。
     * @return 应该结果对象，包含聚合计算后的分组Map列表。
     */
    @ApiOperation(hidden = true, value = "aggregateBy")
    @PostMapping("/aggregateBy")
    public ResponseResult<List<Map<String, Object>>> aggregateBy(@RequestBody MyAggregationParam aggregationParam) {
        return super.baseAggregateBy(aggregationParam);
    }

    /**
     * 获取指定角色Id集合的用户数据集合。
     * @param roleIds 角色Id集合。
     * @return 应该结果对象，包含查询后的用户列表。
     */
    @ApiOperation(hidden = true, value = "getSysUserListByRoleIds")
    @GetMapping("/getSysUserListByRoleIds")
    public ResponseResult<List<SysUserVo>> getSysUserListByRoleIds(@RequestParam Set<Long> roleIds) {
        List<SysUserVo> resultList = new LinkedList<>();
        for (Long roleId : roleIds) {
            List<SysUser> userList = sysUserService.getSysUserListByRoleId(roleId, null, null);
            if (CollUtil.isNotEmpty(userList)) {
                resultList.addAll(SysUser.INSTANCE.fromModelList(userList));
            }
        }
        return ResponseResult.success(resultList);
    }

    /**
     * 获取指定部门Id集合的用户数据集合。
     * @param deptIds 部门Id集合。
     * @return 应该结果对象，包含查询后的用户列表。
     */
    @ApiOperation(hidden = true, value = "getSysUserListByDeptIds")
    @GetMapping("/getSysUserListByDeptIds")
    public ResponseResult<List<SysUserVo>> getSysUserListByDeptIds(@RequestParam Set<Long> deptIds) {
        List<SysUserVo> resultList = new LinkedList<>();
        for (Long deptId : deptIds) {
            SysUser filter = new SysUser();
            filter.setDeptId(deptId);
            List<SysUser> userList = sysUserService.getSysUserList(filter, null);
            if (CollUtil.isNotEmpty(userList)) {
                resultList.addAll(SysUser.INSTANCE.fromModelList(userList));
            }
        }
        return ResponseResult.success(resultList);
    }
}
