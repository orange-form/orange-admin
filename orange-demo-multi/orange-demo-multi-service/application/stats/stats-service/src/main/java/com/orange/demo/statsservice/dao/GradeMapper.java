package com.orange.demo.statsservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.statsservice.model.Grade;

import java.util.*;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface GradeMapper extends BaseDaoMapper<Grade> {

    /**
     * 批量插入对象列表。
     *
     * @param gradeList 新增对象列表。
     */
    void insertList(List<Grade> gradeList);
}
