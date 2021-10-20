package com.flow.demo.common.online.util;

import com.flow.demo.common.core.util.DataSourceResolver;
import com.flow.demo.common.online.model.OnlineDatasourceRelation;
import com.flow.demo.common.online.model.OnlineDblink;
import com.flow.demo.common.online.model.OnlineDict;
import com.flow.demo.common.online.model.OnlineTable;
import com.flow.demo.common.online.service.OnlineDblinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 目前仅仅应用于在线表单服务对象的多数据源切换的动态解析。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Component
public class OnlineDataSourceResolver implements DataSourceResolver {

    @Autowired
    private OnlineDblinkService onlineDblinkService;

    /**
     * 动态解析方法。
     * 先判断第一个参数的类型，在根据具体的类型去获取dblinkId，并根据该值进行多数据源的切换。
     *
     * @param arg        可选的入参。MyDataSourceResolver注解中的arg参数。
     * @param methodArgs 被织入方法的所有参数。
     * @return 返回用于多数据源切换的类型值。DataSourceResolveAspect 切面方法会根据该返回值和配置信息，进行多数据源切换。
     */
    @Override
    public int resolve(String arg, Object[] methodArgs) {
        Serializable id;
        if (methodArgs[0] instanceof OnlineTable) {
            id = ((OnlineTable) methodArgs[0]).getDblinkId();
        } else if (methodArgs[0] instanceof OnlineDict) {
            id = ((OnlineDict) methodArgs[0]).getDblinkId();
        } else if (methodArgs[0] instanceof OnlineDatasourceRelation) {
            id = ((OnlineDatasourceRelation) methodArgs[0]).getSlaveTable().getDblinkId();
        } else {
            throw new IllegalArgumentException("动态表单操作服务方法，不支持类型 ["
                    + methodArgs[0].getClass().getSimpleName() + "] 作为第一个参数！");
        }
        OnlineDblink dblink = onlineDblinkService.getById(id);
        return dblink.getDblinkConfigConstant();
    }
}
