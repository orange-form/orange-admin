package com.orange.demo.webadmin.app.dao;

import com.orange.demo.common.core.base.dao.BaseDaoMapper;
import com.orange.demo.webadmin.app.model.SchoolInfo;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 校区数据数据操作访问接口。
 *
 * @author Jerry
 * @date 2020-09-24
 */
public interface SchoolInfoMapper extends BaseDaoMapper<SchoolInfo> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param schoolInfoFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<SchoolInfo> getSchoolInfoList(
            @Param("schoolInfoFilter") SchoolInfo schoolInfoFilter, @Param("orderBy") String orderBy);
}
