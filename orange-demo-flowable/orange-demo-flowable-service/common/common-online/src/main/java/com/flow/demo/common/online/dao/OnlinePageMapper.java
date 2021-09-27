package com.flow.demo.common.online.dao;

import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.online.model.OnlinePage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 在线表单页面数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlinePageMapper extends BaseDaoMapper<OnlinePage> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlinePageFilter 主表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlinePage> getOnlinePageList(
            @Param("onlinePageFilter") OnlinePage onlinePageFilter, @Param("orderBy") String orderBy);

    /**
     /**
     * 根据数据源Id，返回使用该数据源的OnlinePage对象。
     *
     * @param datasourceId 数据源Id。
     * @return 使用该数据源的页面列表。
     */
    List<OnlinePage> getOnlinePageListByDatasourceId(@Param("datasourceId") Long datasourceId);
}
