package com.flow.demo.common.flow.dao;

import com.flow.demo.common.core.annotation.EnableDataPerm;
import com.flow.demo.common.core.base.dao.BaseDaoMapper;
import com.flow.demo.common.flow.model.FlowWorkOrder;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 工作流工单表数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@EnableDataPerm
public interface FlowWorkOrderMapper extends BaseDaoMapper<FlowWorkOrder> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param flowWorkOrderFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<FlowWorkOrder> getFlowWorkOrderList(
            @Param("flowWorkOrderFilter") FlowWorkOrder flowWorkOrderFilter, @Param("orderBy") String orderBy);
}
