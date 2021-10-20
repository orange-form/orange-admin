package com.orange.demo.webadmin.app.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.webadmin.app.model.ClassStudent;

import java.util.*;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface ClassStudentMapper extends BaseDaoMapper<ClassStudent> {

    /**
     * 批量插入对象列表。
     *
     * @param classStudentList 新增对象列表。
     */
    void insertList(List<ClassStudent> classStudentList);
}
