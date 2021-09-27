package com.flow.demo.common.online.dao;

import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.online.model.OnlineDatasource;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 数据模型数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineDatasourceMapper extends BaseDaoMapper<OnlineDatasource> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineDatasourceFilter 主表过滤对象。
     * @param orderBy                排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineDatasource> getOnlineDatasourceList(
            @Param("onlineDatasourceFilter") OnlineDatasource onlineDatasourceFilter, @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表数据列表。
     *
     * @param pageId                 关联主表Id。
     * @param onlineDatasourceFilter 从表过滤对象。
     * @param orderBy                排序字符串，order by从句的参数。
     * @return 从表数据列表。
     */
    List<OnlineDatasource> getOnlineDatasourceListByPageId(
            @Param("pageId") Long pageId,
            @Param("onlineDatasourceFilter") OnlineDatasource onlineDatasourceFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表中没有和主表建立关联关系的数据列表。
     *
     * @param pageId                 关联主表Id。
     * @param onlineDatasourceFilter 过滤对象。
     * @param orderBy                排序字符串，order by从句的参数。
     * @return 与主表没有建立关联的从表数据列表。
     */
    List<OnlineDatasource> getNotInOnlineDatasourceListByPageId(
            @Param("pageId") Long pageId,
            @Param("onlineDatasourceFilter") OnlineDatasource onlineDatasourceFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据在线表单Id集合，获取关联的在线数据源对象列表。
     *
     * @param formIdSet 在线表单Id集合。
     * @return 与参数表单Id关联的数据源列表。
     */
    List<OnlineDatasource> getOnlineDatasourceListByFormIds(@Param("formIdSet") Set<Long> formIdSet);
}
