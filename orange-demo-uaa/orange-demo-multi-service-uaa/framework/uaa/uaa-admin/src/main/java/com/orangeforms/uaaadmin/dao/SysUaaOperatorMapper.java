package com.orangeforms.uaaadmin.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.uaaadmin.model.SysUaaOperator;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 操作员数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysUaaOperatorMapper extends BaseDaoMapper<SysUaaOperator> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param sysUaaOperatorFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<SysUaaOperator> getSysUaaOperatorList(
            @Param("sysUaaOperatorFilter") SysUaaOperator sysUaaOperatorFilter, @Param("orderBy") String orderBy);
}
