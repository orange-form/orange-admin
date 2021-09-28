package com.orange.demo.webadmin.app.controller;

import com.alibaba.fastjson.JSONObject;
import cn.jimmyshi.beanquery.BeanQuery;
import com.orange.demo.webadmin.app.dto.GradeDto;
import com.orange.demo.webadmin.app.model.Grade;
import com.orange.demo.webadmin.app.service.GradeService;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 年级操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Api(tags = "年级管理接口")
@Slf4j
@RestController
@RequestMapping("/admin/app/grade")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * 新增年级数据。
     *
     * @param gradeDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @ApiOperationSupport(ignoreParameters = {"gradeDto.gradeId"})
    @PostMapping("/add")
    public ResponseResult<Integer> add(@MyRequestBody GradeDto gradeDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(gradeDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Grade grade = MyModelUtil.copyTo(gradeDto, Grade.class);
        grade = gradeService.saveNew(grade);
        return ResponseResult.success(grade.getGradeId());
    }

    /**
     * 更新年级数据。
     *
     * @param gradeDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody GradeDto gradeDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(gradeDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Grade grade = MyModelUtil.copyTo(gradeDto, Grade.class);
        Grade originalGrade = gradeService.getById(grade.getGradeId());
        if (originalGrade == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (!gradeService.update(grade, originalGrade)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除年级数据。
     *
     * @param gradeId 删除对象主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody Integer gradeId) {
        if (MyCommonUtil.existBlankArgument(gradeId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        if (!gradeService.remove(gradeId)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 白名单接口，登录用户均可访问。以字典形式返回全部年级数据集合。
     * 所有数据全部取自于缓存，对于数据库中存在，但是缓存中不存在的数据，不会返回。
     *
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict() {
        List<Grade> resultList = gradeService.getAllListFromCache();
        if (CollectionUtils.isEmpty(resultList)) {
            gradeService.reloadCachedData(true);
            resultList = gradeService.getAllList();
        }
        return ResponseResult.success(BeanQuery.select(
                "gradeId as id", "gradeName as name").executeFrom(resultList));
    }

    /**
     * 白名单接口，登录用户均可访问。以字典形式返回全部年级数据集合。
     * fullResultList中的字典列表全部取自于数据库，而cachedResultList全部取自于缓存，前端负责比对。
     *
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listAll")
    public ResponseResult<JSONObject> listAll() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fullResultList", BeanQuery.select(
                "gradeId as id", "gradeName as name").executeFrom(gradeService.getAllList()));
        jsonObject.put("cachedResultList", BeanQuery.select(
                "gradeId as id", "gradeName as name").executeFrom(gradeService.getAllListFromCache()));
        return ResponseResult.success(jsonObject);
    }

    /**
     * 根据字典Id集合，获取查询后的字典数据。
     *
     * @param dictIds 字典Id集合。
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @PostMapping("/listDictByIds")
    public ResponseResult<List<Map<String, Object>>> listDictByIds(
            @MyRequestBody(elementType = Integer.class) List<Integer> dictIds) {
        List<Grade> resultList = gradeService.getInList(new HashSet<>(dictIds));
        return ResponseResult.success(BeanQuery.select(
                "gradeId as id", "gradeName as name").executeFrom(resultList));
    }

    /**
     * 将当前字典表的数据重新加载到缓存中。
     * 由于缓存的数据更新，在add/update/delete等接口均有同步处理。因此该接口仅当同步过程中出现问题时，
     * 可手工调用，或者每天晚上定时同步一次。
     */
    @GetMapping("/reloadCachedData")
    public ResponseResult<Boolean> reloadCachedData() {
        gradeService.reloadCachedData(true);
        return ResponseResult.success(true);
    }
}
