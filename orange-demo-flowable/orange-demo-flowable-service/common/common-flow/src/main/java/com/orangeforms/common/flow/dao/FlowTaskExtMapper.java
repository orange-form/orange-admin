package com.orangeforms.common.flow.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.flow.model.FlowTaskExt;

import java.util.List;

/**
 * 流程任务扩展数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowTaskExtMapper extends BaseDaoMapper<FlowTaskExt> {

    /**
     * 批量插入流程任务扩展信息列表。
     *
     * @param flowTaskExtList 流程任务扩展信息列表。
     */
    void insertList(List<FlowTaskExt> flowTaskExtList);
}
