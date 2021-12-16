package com.orangeforms.common.online.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.online.model.OnlineTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据表数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineTableMapper extends BaseDaoMapper<OnlineTable> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineTableFilter 主表过滤对象。
     * @param orderBy           排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineTable> getOnlineTableList(
            @Param("onlineTableFilter") OnlineTable onlineTableFilter, @Param("orderBy") String orderBy);

    /**
     * 根据数据源Id，获取该数据源及其关联所引用的数据表列表。
     *
     * @param datasourceId 指定的数据源Id。
     * @return 该数据源及其关联所引用的数据表列表。
     */
    List<OnlineTable> getOnlineTableListByDatasourceId(@Param("datasourceId") Long datasourceId);
}
