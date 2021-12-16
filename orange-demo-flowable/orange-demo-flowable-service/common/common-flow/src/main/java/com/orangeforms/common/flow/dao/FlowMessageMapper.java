package com.orangeforms.common.flow.dao;

import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import com.orangeforms.common.flow.model.FlowMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 工作流消息数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowMessageMapper extends BaseDaoMapper<FlowMessage> {

    /**
     * 获取指定用户和身份分组Id集合的催办消息列表。
     *
     * @param loginName  用户的登录名。与流程任务的assignee精确匹配。
     * @param groupIdSet 用户身份分组Id集合。
     * @return 查询后的催办消息列表。
     */
    List<FlowMessage> getRemindingMessageListByUser(
            @Param("loginName") String loginName, @Param("groupIdSet") Set<String> groupIdSet);
}
