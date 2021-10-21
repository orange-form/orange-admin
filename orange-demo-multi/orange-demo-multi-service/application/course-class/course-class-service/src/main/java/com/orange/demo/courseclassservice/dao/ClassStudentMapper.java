package com.orange.demo.courseclassservice.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.courseclassservice.model.ClassStudent;

import java.util.*;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface ClassStudentMapper extends BaseDaoMapper<ClassStudent> {

    /**
     * 批量插入对象列表。
     *
     * @param classStudentList 新增对象列表。
     */
    void insertList(List<ClassStudent> classStudentList);
}
