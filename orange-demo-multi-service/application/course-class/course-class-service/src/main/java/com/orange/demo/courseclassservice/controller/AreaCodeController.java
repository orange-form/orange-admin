package com.orange.demo.courseclassservice.controller;

import io.swagger.annotations.Api;
import cn.jimmyshi.beanquery.BeanQuery;
import com.orange.demo.common.core.base.controller.BaseController;
import com.orange.demo.common.core.base.service.BaseService;
import com.orange.demo.common.core.util.MyModelUtil;
import com.orange.demo.common.core.object.MyQueryParam;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.courseclassinterface.dto.AreaCodeDto;
import com.orange.demo.courseclassservice.model.AreaCode;
import com.orange.demo.courseclassservice.service.AreaCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 行政区划数据访问接口类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Api(tags = "行政区划数据访问接口")
@RestController
@RequestMapping("/areaCode")
public class AreaCodeController extends BaseController<AreaCode, AreaCodeDto, Long> {

    @Autowired
    private AreaCodeService areaCodeService;

    @Override
    protected BaseService<AreaCode, AreaCodeDto, Long> service() {
        return areaCodeService;
    }

    /**
     * 按照字典的形式返回行政区划列表。
     *
     * @return 字典形式的行政区划列表。
     */
    @GetMapping("/listDictAreaCode")
    public ResponseResult<List<Map<String, Object>>> listDictAreaCode() {
        List<AreaCode> resultList = areaCodeService.getAllList();
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "areaId as id", "areaName as name").executeFrom(resultList));
    }

    /**
     * 根据上级行政区划Id获取其下级行政区划列表。
     *
     * @param parentId 上级行政区划Id。
     * @return 按照字典的形式返回下级行政区划列表。
     */
    @GetMapping("/listDictAreaCodeByParentId")
    public ResponseResult<List<Map<String, Object>>> listDictAreaCodeByParentId(@RequestParam(required = false) Long parentId) {
        Collection<AreaCode> resultList = areaCodeService.getListByParentId(parentId);
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseResult.success(new LinkedList<>());
        }
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "areaId as id", "areaName as name").executeFrom(resultList));
    }

    /**
     * 查看单条记录的详情信息，如果当前主对象包含字典和一对一关联，也都同时返回。
     *
     * @param areaId 行政区划Id。
     * @return 应答结果的Data对象中，将包含行政区划对象。
     */
    @GetMapping("/view")
    public ResponseResult<AreaCodeDto> view(@RequestParam Long areaId) {
        AreaCode areaCode = areaCodeService.getById(areaId);
        return ResponseResult.success(MyModelUtil.copyTo(areaCode, AreaCodeDto.class));
    }

    /**
     * 获取存在于主键Id列表的数据集合，该方法主要用于微服务间远程数据调用。
     *
     * @param areaCodeIds    主键Id列表。
     * @param withDict       该字段只是为了保证和其他对象接口一致，在这里没有实际用处。
     * @return 符合主键(in list)的数据集合。
     */
    @PostMapping("/listByIds")
    public ResponseResult<List<AreaCodeDto>> listByIds(
            @RequestParam Set<Long> areaCodeIds, @RequestParam Boolean withDict) {
        return super.baseListByIds(areaCodeIds, withDict, null);
    }

    /**
     * 基于主键Id，获取远程对象，该方法主要用于微服务间远程数据调用。
     *
     * @param areaId   主键Id。
     * @param withDict 该字段只是为了保证和其他对象接口一致，在这里没有实际用处。
     * @return 应答结果对象，包含主对象数据。
     */
    @GetMapping("/getById")
    public ResponseResult<AreaCodeDto> getById(@RequestParam Long areaId, @RequestParam Boolean withDict) {
        return super.baseGetById(areaId, withDict, null);
    }

    /**
     * 判断参数列表中指定的主键Id集合，是否全部存在。仅限于微服务间远程接口调用。
     *
     * @param areaCodeIds 主键Id集合。
     * @return 应答结果对象，包含true全部存在，否则false。
     */
    @GetMapping("/existIds")
    public ResponseResult<Boolean> existIds(@RequestParam Set<Long> areaCodeIds) {
        return super.baseExistIds(areaCodeIds);
    }

    /**
     * 判断指定主键Id的对象是否存在，该方法主要用于微服务间远程数据调用。
     *
     * @param areaId 主键Id。
     * @return 存在返回true，否则false。
     */
    @GetMapping("/existId")
    public ResponseResult<Boolean> existId(@RequestParam Long areaId) {
        return super.baseExistId(areaId);
    }

    /**
     * 复杂的查询调用，包括(in list)过滤，对象条件过滤，分组和排序等。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/listBy")
    public ResponseResult<List<AreaCodeDto>> listBy(@RequestBody MyQueryParam queryParam) {
        return super.baseListBy(queryParam, null);
    }

    /**
     * 复杂的查询调用，仅返回单体记录。主要用于微服务间远程过程调用。
     *
     * @param queryParam 查询参数。
     * @return 应答结果对象，包含符合查询过滤条件的对象结果集。
     */
    @PostMapping("/getBy")
    public ResponseResult<AreaCodeDto> getBy(@RequestBody MyQueryParam queryParam) {
        return super.baseGetBy(queryParam, null);
    }
}
