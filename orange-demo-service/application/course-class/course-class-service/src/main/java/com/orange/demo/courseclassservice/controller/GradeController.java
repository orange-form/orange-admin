package com.orange.demo.courseclassservice.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.BaseDictService;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.object.MyQueryParam;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.courseclassinterface.dto.GradeDto;
import com.orange.demo.courseclassservice.model.Grade;
import com.orange.demo.courseclassservice.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.*;

/**
 * 年级操作控制器类。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/grade")
public class GradeController extends BaseController<Grade, GradeDto, Integer> {

    @Autowired
    private GradeService gradeService;

    @Override
    protected BaseDictService<Grade, GradeDto, Integer> service() {
        return gradeService;
    }

    /**
     * 新增年级数据。
     *
     * @param gradeDto 新增对象。
     * @return 应答结果对象，包含新增对象主键Id。
     */
    @PostMapping("/add")
    public ResponseResult<JSONObject> add(@MyRequestBody("grade") GradeDto gradeDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(gradeDto);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
        }
        Grade grade = MyModelUtil.copyTo(gradeDto, Grade.class);
        grade = gradeService.saveNew(grade);
        JSONObject responseData = new JSONObject();
        responseData.put("gradeId", grade.getGradeId());
        return ResponseResult.success(responseData);
    }

    /**
     * 更新年级数据。
     *
     * @param gradeDto 更新对象。
     * @return 应答结果对象。
     */
    @PostMapping("/update")
    public ResponseResult<Void> update(@MyRequestBody("grade") GradeDto gradeDto) {
        String errorMessage = MyCommonUtil.getModelValidationError(gradeDto, Default.class, UpdateGroup.class);
        if (errorMessage != null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATAED_FAILED, errorMessage);
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
     * 查看指定年级对象详情。
     *
     * @param gradeId 指定对象主键Id。
     * @return 应答结果对象，包含对象详情。
     */
    @GetMapping("/view")
    public ResponseResult<GradeDto> view(@RequestParam Integer gradeId) {
        if (MyCommonUtil.existBlankArgument(gradeId)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        Grade grade = gradeService.getById(gradeId);
        if (grade == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        GradeDto gradeDto = MyModelUtil.copyTo(grade, GradeDto.class);
        return ResponseResult.success(gradeDto);
    }
    
    /**
     * 以字典形式返回全部年级数据集合。
     * 白名单接口，登录用户均可访问。
     *
     * @return 应答结果对象，包含字典形式的数据集合。
     */
    @GetMapping("/listDictGrade")
    public ResponseResult<List<Map<String, Object>>> listDictGrade() {
        List<Grade> resultList = gradeService.getAllList();
        return ResponseResult.success(BeanQuery.select(
                "gradeId as id", "gradeName as name").executeFrom(resultList));
    }

    /**
     * 根据主键Id集合，获取数据对象集合。仅限于微服务间远程接口调用。
     *
     * @param gradeIds 主键Id集合。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象集合。
     */
    @PostMapping("/listByIds")
    public ResponseResult<List<GradeDto>> listByIds(
            @RequestParam Set<Integer> gradeIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(gradeIds, withDict, null);
    }

    /**
     * 根据主键Id，获取数据对象。仅限于微服务间远程接口调用。
     *
     * @param gradeId 主键Id。
     * @param withDict 是否包含字典关联。
     * @return 应答结果对象，包含主对象数据。
     */
    @PostMapping("/getById")
    public ResponseResult<GradeDto> getById(
            @RequestParam Integer gradeId, @RequestParam Boolean withDict) {
        return super.baseGetById(gradeId, withDict, null);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。主要用于微服务间远程过程调用。
     *
     * @param gradeIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @PostMapping("/existIds")
    public ResponseResult<Boolean> existIds(@RequestParam Set<Integer> gradeIds) {
        return super.baseExistIds(gradeIds);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。主要用于微服务间远程过程调用。
     *
     * @param gradeId 主键Id。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @PostMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Integer gradeId) {
        return super.baseExistId(gradeId);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/listBy")
    public ResponseResult<List<GradeDto>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, null);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/getBy")
    public ResponseResult<GradeDto> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, null);
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
