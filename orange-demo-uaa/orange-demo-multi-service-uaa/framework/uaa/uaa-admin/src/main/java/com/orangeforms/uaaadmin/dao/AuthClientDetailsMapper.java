package com.orangeforms.uaaadmin.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.uaaadmin.model.AuthClientDetails;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 应用客户端数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface AuthClientDetailsMapper extends BaseDaoMapper<AuthClientDetails> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param authClientDetailsFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<AuthClientDetails> getAuthClientDetailsList(
            @Param("authClientDetailsFilter") AuthClientDetails authClientDetailsFilter, @Param("orderBy") String orderBy);
}
