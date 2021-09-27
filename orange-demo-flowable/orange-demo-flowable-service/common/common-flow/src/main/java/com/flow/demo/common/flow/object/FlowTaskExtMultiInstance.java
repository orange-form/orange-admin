package com.flow.demo.common.flow.object;

import lombok.Data;

import java.util.List;


/**
 * 流程任务扩展属性中，表示多实例任务的扩展对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowTaskExtMultiInstance {

    public static final String ASSIGNEE_TYPE_ASSIGNEE = "assignee";
    public static final String ASSIGNEE_TYPE_GROUPID = "groupId";

    /**
     * 指派人类型。目前支持 assignee、groupId。
     */
    private String assigneeType;

    /**
     * 指派人登录名列表。
     */
    private List<String> assigneeList;
}
