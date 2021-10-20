package com.flow.demo.common.online.dao;

import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.online.model.OnlineVirtualColumn;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 虚拟字段数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineVirtualColumnMapper extends BaseDaoMapper<OnlineVirtualColumn> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineVirtualColumnFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineVirtualColumn> getOnlineVirtualColumnList(
            @Param("onlineVirtualColumnFilter") OnlineVirtualColumn onlineVirtualColumnFilter, @Param("orderBy") String orderBy);
}
