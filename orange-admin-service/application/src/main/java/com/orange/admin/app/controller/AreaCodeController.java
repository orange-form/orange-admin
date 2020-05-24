package com.orange.admin.app.controller;

import cn.jimmyshi.beanquery.BeanQuery;
import com.orange.admin.app.model.AreaCode;
import com.orange.admin.app.service.AreaCodeService;
import com.orange.admin.common.core.object.ResponseResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 行政区划数据访问接口类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
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
}
