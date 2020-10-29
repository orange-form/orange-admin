package com.orange.demo.app.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.github.pagehelper.page.PageMethod;
import com.orange.demo.app.model.*;
import com.orange.demo.app.service.*;
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
     * @param schoolInfo 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"schoolInfo.userId"})
    @PostMapping("/add")
    public ResponseResult<Long> add(@MyRequestBody SchoolInfo schoolInfo) {
        String errorMessage = MyCommonUtil.getModelValidationError(schoolInfo);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = schoolInfoService.verifyRelatedData(schoolInfo, null);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        schoolInfo = schoolInfoService.saveNew(schoolInfo);
        return ResponseResult.success(schoolInfo.getSchoolId());
    }

    /**
     * 更新校区数据数据。
     *
     * @param schoolInfo 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody SchoolInfo schoolInfo) {
        String errorMessage = MyCommonUtil.getModelValidationError(schoolInfo, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        // 验证关联Id的数据合法性
        SchoolInfo originalSchoolInfo = schoolInfoService.getById(schoolInfo.getSchoolId());
        if (originalSchoolInfo == null) {
            //NOTE: 修改下面方括号中的话述
            errorMessage = "数据验证失败，当前 [数据] 并不存在，请刷新后重试！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        // 验证关联Id的数据合法性
        CallResult callResult = schoolInfoService.verifyRelatedData(schoolInfo, originalSchoolInfo);
        if (!callResult.isSuccess()) {
            errorMessage = callResult.getErrorMessage();
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
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
     * @param schoolInfoFilter 过滤对象。
     * @param orderParam 排序参数。
     * @param pageParam 分页参数。
     * @return 应答结果对象，包含查询结果集。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<SchoolInfo>> list(
            @MyRequestBody SchoolInfo schoolInfoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SchoolInfo.class);
        List<SchoolInfo> resultList = schoolInfoService.getSchoolInfoListWithRelation(schoolInfoFilter, orderBy);
        return ResponseResult.success(MyPageUtil.makeResponseData(resultList));
    }

    /**
     * 查看指定校区数据对象详情。
     *
     * @param schoolId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<SchoolInfo> view(@RequestParam Long schoolId) {
        if (MyCommonUtil.existBlankArgument(schoolId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SchoolInfo schoolInfo = schoolInfoService.getByIdWithRelation(schoolId, MyRelationParam.full());
        if (schoolInfo == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(schoolInfo);
    }

    /**
     * 以字典形式返回全部校区数据数据集合。字典的键值为[schoolId, schoolName]。
     * 白名单接口，登录用户均可访问。
     *
     * @param filter 过滤对象。
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/listDictSchoolInfo")
    public ResponseResult<List<Map<String, Object>>> listDictSchoolInfo(SchoolInfo filter) {
        List<SchoolInfo> resultList = schoolInfoService.getListByFilter(filter, null);
        return ResponseResult.success(BeanQuery.select(
                "schoolId as id", "schoolName as name").executeFrom(resultList));
    }
}
