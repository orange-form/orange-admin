package com.orange.demo.upmsservice.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.upmsservice.model.*;
import com.orange.demo.upmsservice.service.*;
import com.orange.demo.upmsinterface.dto.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.AddGroup;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.upmsservice.config.ApplicationConfig;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
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
public class SysUserController extends BaseController<SysUser, SysUserDto, Long> {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ApplicationConfig appConfig;

    @Override
    protected BaseService<SysUser, SysUserDto, Long> service() {
        return sysUserService;
    }

    /**
     * 新增用户操作。
     *
     * @param sysUserDto       新增用户对象。
     * @param roleIdListString 逗号分隔的角色Id列表。
     * @return 应答结果对象，包含新增用户的主键Id。
     */
    @SuppressWarnings("unchecked")
    @ApiOperationSupport(ignoreParameters = {
            "sysUser.userId",
            "sysUser.createTimeStart",
            "sysUser.createTimeEnd"})
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @MyRequestBody("sysUser") SysUserDto sysUserDto, @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUserDto, Default.class, AddGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysUser sysUser = SysUser.INSTANCE.toModel(sysUserDto);
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
     * @param sysUserDto       更新用户对象。
     * @param roleIdListString 逗号分隔的角色Id列表。
     * @return 应答结果对象。
     */
    @SuppressWarnings("unchecked")
    @ApiOperationSupport(ignoreParameters = {
            "sysUser.createTimeStart",
            "sysUser.createTimeEnd"})
    @PostMapping("/update")
    public ResponseResult<Void> update(
            @MyRequestBody("sysUser") SysUserDto sysUserDto, @MyRequestBody String roleIdListString) {
        String errorMessage = MyCommonUtil.getModelValidationError(sysUserDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        SysUser originalUser = sysUserService.getById(sysUserDto.getUserId());
        if (originalUser == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysUser sysUser = SysUser.INSTANCE.toModel(sysUserDto);
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
            //NOTE: 修改下面方括号中的话述
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
    public ResponseResult<MyPageData<SysUserDto>> list(
            @MyRequestBody("sysUserFilter") SysUserDto sysUserDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SysUser sysUserFilter = SysUser.INSTANCE.toModel(sysUserDtoFilter);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class);
        List<SysUser> sysUserList =
                sysUserService.getSysUserListWithRelation(sysUserFilter, orderBy);
        long totalCount = 0L;
        if (sysUserList instanceof Page) {
            totalCount = ((Page<SysUser>) sysUserList).getTotal();
        }
        // 分页连同对象数据转换copy工作，下面的方法一并完成。
        Tuple2<List<SysUserDto>, Long> responseData =
                new Tuple2<>(SysUser.INSTANCE.fromModelList(sysUserList), totalCount);
        return ResponseResult.success(MyPageUtil.makeResponseData(responseData));
    }

    /**
     * 查看指定用户管理对象详情。
     *
     * @param userId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SysUserDto> view(@RequestParam Long userId) {
        if (MyCommonUtil.existBlankArgument(userId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 这里查看用户数据时候，需要把用户多对多关联的角色和数据权限Id一并查出。
        SysUser sysUser =
                sysUserService.getByIdWithRelation(userId, MyRelationParam.full());
        if (sysUser == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SysUserDto sysUserDto = SysUser.INSTANCE.fromModel(sysUser);
        return ResponseResult.success(sysUserDto);
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
    public ResponseResult<List<SysUserDto>> listByIds(
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
    public ResponseResult<SysUserDto> getById(
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
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @ApiOperation(hidden = true, value = "listBy")
    @PostMapping("/listBy")
    public ResponseResult<List<SysUserDto>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, SysUser.INSTANCE);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @ApiOperation(hidden = true, value = "listMapBy")
    @PostMapping("/listMapBy")
    public ResponseResult<List<Map<String, Object>>> listMapBy(@RequestBody MyQueryParam queryParam) {
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
    public ResponseResult<SysUserDto> getBy(@RequestBody MyQueryParam queryParam) {
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
}
