package com.orange.demo.courseclassservice.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.courseclassservice.model.*;
import com.orange.demo.courseclassservice.service.*;
import com.orange.demo.courseclassapi.dto.*;
import com.orange.demo.courseclassapi.vo.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.IBaseService;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 校区数据操作控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "校区数据管理接口")
@Slf4j
@RestController
@RequestMapping("/schoolInfo")
public class SchoolInfoController extends BaseController<SchoolInfo, SchoolInfoVo, Long> {

    @Autowired
    private SchoolInfoService schoolInfoService;

    @Override
    protected IBaseService<SchoolInfo, Long> service() {
        return schoolInfoService;
    }

    /**
     * 新增校区数据数据。
     *
     * @param schoolInfoDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"schoolInfoDto.schoolId"})
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SchoolInfoDto schoolInfoDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(schoolInfoDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SchoolInfo schoolInfo = MyModelUtil.copyTo(schoolInfoDto, SchoolInfo.class);
        // 验证关联Id的数据合法性
        CallResult callResult = schoolInfoService.verifyRelatedData(schoolInfo, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        schoolInfo = schoolInfoService.saveNew(schoolInfo);
        return ResponseResult.success(schoolInfo.getSchoolId());
    }

    /**
     * 更新校区数据数据。
     *
     * @param schoolInfoDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SchoolInfoDto schoolInfoDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(schoolInfoDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        SchoolInfo schoolInfo = MyModelUtil.copyTo(schoolInfoDto, SchoolInfo.class);
        SchoolInfo originalSchoolInfo = schoolInfoService.getById(schoolInfo.getSchoolId());
        if (originalSchoolInfo == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = schoolInfoService.verifyRelatedData(schoolInfo, originalSchoolInfo);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!schoolInfoService.update(schoolInfo, originalSchoolInfo)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除校区数据数据。
     *
     * @param schoolId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Long schoolId) {
        String errorMessage;
        if (MyCommonUtil.existBlankArgument(schoolId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 验证关联Id的数据合法性
        SchoolInfo originalSchoolInfo = schoolInfoService.getById(schoolId);
        if (originalSchoolInfo == null) {
            // NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [对象] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!schoolInfoService.remove(schoolId)) {
            errorMessage = "数据操作失败，删除的对象不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        return ResponseResult.success();
    }

    /**
     * 列出符合过滤条件的校区数据列表。
     *
     * @param schoolInfoDtoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SchoolInfoVo>> list(
            @MyRequestBody SchoolInfoDto schoolInfoDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        SchoolInfo schoolInfoFilter = MyModelUtil.copyTo(schoolInfoDtoFilter, SchoolInfo.class);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SchoolInfo.class);
        List<SchoolInfo> schoolInfoList =
                schoolInfoService.getSchoolInfoListWithRelation(schoolInfoFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(schoolInfoList, SchoolInfo.INSTANCE));
    }

    /**
     * 查看指定校区数据对象详情。
     *
     * @param schoolId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SchoolInfoVo> view(@RequestParam Long schoolId) {
        if (MyCommonUtil.existBlankArgument(schoolId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SchoolInfo schoolInfo =
                schoolInfoService.getByIdWithRelation(schoolId, MyRelationParam.full());
        if (schoolInfo == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        SchoolInfoVo schoolInfoVo = SchoolInfo.INSTANCE.fromModel(schoolInfo);
        return ResponseResult.success(schoolInfoVo);
    }

    /**
     * 以字典形式返回全部校区数据数据集合。字典的键值为[schoolId, schoolName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(SchoolInfo filter) {
        List<SchoolInfo> resultList = schoolInfoService.getListByFilter(filter);
        return ResponseResult.success(
                BeanQuery.select("schoolId as id", "schoolName as name").executeFrom(resultList));
    }

    /**
     * 根据字典Id集合，获取查询后的字典数据。
     *
     * @param dictIds 字典Id集合。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @PostMapping("/listDictByIds")
    public ResponseResult<List<Map<String, Object>>> listDictByIds(
            @MyRequestBody(elementType = Long.class) List<Long> dictIds) {
        List<SchoolInfo> resultList = schoolInfoService.getInList(new HashSet<>(dictIds));
        return ResponseResult.success(
                BeanQuery.select("schoolId as id", "schoolName as name").executeFrom(resultList));
    }

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param schoolIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    @ApiOperation(hidden = true, value = "listByIds")
    @PostMapping("/listByIds")
    public ResponseResult<List<SchoolInfoVo>> listByIds(
            @RequestParam Set<Long> schoolIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(schoolIds, withDict, SchoolInfo.INSTANCE);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param schoolId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @ApiOperation(hidden = true, value = "getById")
    @PostMapping("/getById")
    public ResponseResult<SchoolInfoVo> getById(
            @RequestParam Long schoolId, @RequestParam Boolean withDict) {
        return super.baseGetById(schoolId, withDict, SchoolInfo.INSTANCE);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param schoolIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existIds")
    @PostMapping("/existIds")
    public ResponseResult<Boolean> existIds(@RequestParam Set<Long> schoolIds) {
        return super.baseExistIds(schoolIds);
    }

    /**
     * 判断参数列表中指定的主键Id是否存在。仅限于微服务间远程接口调用。
     *
     * @param schoolId 主键Id。
     * @return 应答结果对象，包含true表示存在，否则false。
     */
    @ApiOperation(hidden = true, value = "existId")
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long schoolId) {
        return super.baseExistId(schoolId);
    }

    /**
     * 根据主键Id删除数据。
     *
     * @param schoolId 主键Id。
     * @return 删除数量。
     */
    @ApiOperation(hidden = true, value = "deleteById")
    @PostMapping("/deleteById")
    public ResponseResult<Integer> deleteById(@RequestParam Long schoolId) throws Exception {
        SchoolInfo filter = new SchoolInfo();
        filter.setSchoolId(schoolId);
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
    public ResponseResult<Integer> deleteBy(@RequestBody SchoolInfoDto filter) throws Exception {
        return super.baseDeleteBy(MyModelUtil.copyTo(filter, SchoolInfo.class));
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分页和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 分页数据集合对象。如MyQueryParam参数的分页属性为空，则不会执行分页操作，只是基于MyPageData对象返回数据结果。
     */
    @ApiOperation(hidden = true, value = "listBy")
    @PostMapping("/listBy")
    public ResponseResult<MyPageData<SchoolInfoVo>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, SchoolInfo.INSTANCE);
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
        return super.baseListMapBy(queryParam, SchoolInfo.INSTANCE);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @ApiOperation(hidden = true, value = "getBy")
    @PostMapping("/getBy")
    public ResponseResult<SchoolInfoVo> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, SchoolInfo.INSTANCE);
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
