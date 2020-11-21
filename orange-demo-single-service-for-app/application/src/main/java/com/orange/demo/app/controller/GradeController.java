package com.orange.demo.app.controller;

import com.orange.demo.app.model.*;
import com.orange.demo.app.service.*;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.validator.UpdateGroup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.jimmyshi.beanquery.BeanQuery;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 年级操作控制器类。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Slf4j
@RestController
@RequestMapping("/admin/app/grade")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * 新增年级数据。
     *
     * @param grade 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<Integer> add(@MyRequestBody Grade grade) {
        String errorMessage = MyCommonUtil.getModelValidationError(grade);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        grade = gradeService.saveNew(grade);
        return ResponseResult.success(grade.getGradeId());
    }

    /**
     * 更新年级数据。
     *
     * @param grade 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody Grade grade) {
        String errorMessage = MyCommonUtil.getModelValidationError(grade, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
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
     * 以字典形式返回全部年级数据集合。
     * 白名单接口，登录用户均可访问。
     *
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict() {
        List<Grade> resultList = gradeService.getAllList();
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
