package com.orangeforms.uaaadmin.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.uaaadmin.model.SysUaaUser;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * UAA用户数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
public interface SysUaaUserMapper extends BaseDaoMapper<SysUaaUser> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param sysUaaUserFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<SysUaaUser> getSysUaaUserList(
            @Param("sysUaaUserFilter") SysUaaUser sysUaaUserFilter, @Param("orderBy") String orderBy);
}
