package com.orangeforms.webadmin.app.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.webadmin.app.model.Grade;

import java.util.*;

/**
 * 年级数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface GradeMapper extends BaseDaoMapper<Grade> {

    /**
     * 批量插入对象列表。
     *
     * @param gradeList 新增对象列表。
     */
    void insertList(List<Grade> gradeList);
}
