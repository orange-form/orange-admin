package com.orangeforms.webadmin.app.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.orangeforms.webadmin.app.model.AreaCode;
import com.orangeforms.webadmin.app.service.AreaCodeService;
import com.orangeforms.common.core.object.ResponseResult;
import com.orangeforms.common.core.annotation.MyRequestBody;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 行政区划数据访问接口类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@RestController
@RequestMapping("/admin/app/areaCode")
public class AreaCodeController {

    @Autowired
    private AreaCodeService areaCodeService;

    /**
     * 按照字典的形式返回行政区划列表。
     *
     * @return 字典形式的行政区划列表。
     */
    @GetMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict() {
        List<AreaCode> resultList = areaCodeService.getAllListFromCache();
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "areaId as id", "areaName as name").executeFrom(resultList));
    }

    /**
     * 根据上级行政区划Id获取其下级行政区划列表。
     *
     * @param parentId 上级行政区划Id。
     * @return 按照字典的形式返回下级行政区划列表。
     */
    @GetMapping("/listDictByParentId")
    public ResponseResult<List<Map<String, Object>>> listDictByParentId(@RequestParam(required = false) Long parentId) {
        Collection<AreaCode> resultList = areaCodeService.getListByParentId(parentId);
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseResult.success(new LinkedList<>());
        }
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "areaId as id", "areaName as name").executeFrom(resultList));
    }

    /**
     * 根据字典Id集合，获取查询后的字典数据。
     *
     * @param dictIds 字典Id集合。
     * @return 字典形式的行政区划列表。
     */
    @PostMapping("/listDictByIds")
    public ResponseResult<List<Map<String, Object>>> listDictByIds(
            @MyRequestBody(elementType = Long.class) List<Long> dictIds) {
        List<AreaCode> resultList = areaCodeService.getInList(new HashSet<>(dictIds));
        return ResponseResult.success(BeanQuery.select(
                "parentId as parentId", "areaId as id", "areaName as name").executeFrom(resultList));
    }
}
