package com.orange.demo.webadmin.app.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.webadmin.app.vo.*;
import com.orange.demo.webadmin.app.dto.*;
import com.orange.demo.webadmin.app.model.*;
import com.orange.demo.webadmin.app.service.*;
import com.orange.demo.common.core.object.*;
import com.orange.demo.common.core.util.*;
import com.orange.demo.common.core.constant.*;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.groups.Default;

/**
 * 校区数据操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Api(tags = "校区数据管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/schoolInfo")
public class SchoolInfoController {

    @Autowired
    private SchoolInfoService schoolInfoService;

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
        List<SchoolInfo> schoolInfoList = schoolInfoService.getSchoolInfoListWithRelation(schoolInfoFilter, orderBy);
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
        SchoolInfo schoolInfo = schoolInfoService.getByIdWithRelation(schoolId, MyRelationParam.full());
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
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(SchoolInfo filter) {
        List<SchoolInfo> resultList = schoolInfoService.getListByFilter(filter);
        return ResponseResult.success(BeanQuery.select(
                "schoolId as id", "schoolName as name").executeFrom(resultList));
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
        return ResponseResult.success(BeanQuery.select(
                "schoolId as id", "schoolName as name").executeFrom(resultList));
    }
}
