package com.orange.demo.app.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.app.model.StudentActionTrans;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 学生行为流水数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface StudentActionTransMapper extends BaseDaoMapper<StudentActionTrans> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param studentActionTransFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<StudentActionTrans> getStudentActionTransList(
            @Param("studentActionTransFilter") StudentActionTrans studentActionTransFilter, @Param("orderBy") String orderBy);
}
