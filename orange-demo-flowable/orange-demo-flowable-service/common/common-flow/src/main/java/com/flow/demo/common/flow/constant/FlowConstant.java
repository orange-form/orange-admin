package com.flow.demo.common.flow.constant;

/**
 * 工作流中的常量数据。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class FlowConstant {

    /**
     * 标识流程实例启动用户的变量名。
     */
    public final static String START_USER_NAME_VAR = "${startUserName}";

    /**
     * 流程实例发起人变量名。
     */
    public final static String PROC_INSTANCE_INITIATOR_VAR = "initiator";

    /**
     * 流程实例中发起人用户的变量名。
     */
    public final static String PROC_INSTANCE_START_USER_NAME_VAR = "startUserName";

    /**
     * 操作类型变量。
     */
    public final static String OPERATION_TYPE_VAR = "operationType";

    /**
     * 多任务拒绝数量变量。
     */
    public final static String MULTI_REFUSE_COUNT_VAR = "multiRefuseCount";

    /**
     * 多任务同意数量变量。
     */
    public final static String MULTI_AGREE_COUNT_VAR = "multiAgreeCount";

    /**
     * 多任务弃权数量变量。
     */
    public final static String MULTI_ABSTAIN_COUNT_VAR = "multiAbstainCount";

    /**
     * 会签发起任务。
     */
    public final static String MULTI_SIGN_START_TASK_VAR = "multiSignStartTask";

    /**
     * 会签任务总数量。
     */
    public final static String MULTI_SIGN_NUM_OF_INSTANCES_VAR = "multiNumOfInstances";

    /**
     * 多实例实例数量变量。
     */
    public final static String NUMBER_OF_INSTANCES_VAR = "nrOfInstances";

    /**
     * 多任务指派人列表变量。
     */
    public final static String MULTI_ASSIGNEE_LIST_VAR = "assigneeList";

    /**
     * 上级部门领导审批变量
     */
    public final static String GROUP_TYPE_UP_DEPT_POST_LEADER_VAR = "upDeptPostLeader";

    /**
     * 上级部门领导审批变量
     */
    public final static String GROUP_TYPE_DEPT_POST_LEADER_VAR = "deptPostLeader";

    /**
     * 上级部门领导审批
     */
    public final static String GROUP_TYPE_UP_DEPT_POST_LEADER = "UP_DEPT_POST_LEADER";

    /**
     * 本部门岗位领导审批
     */
    public final static String GROUP_TYPE_DEPT_POST_LEADER = "DEPT_POST_LEADER";
}
