package com.orangeforms.courseclassservice.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.courseclassservice.model.ClassStudent;

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
