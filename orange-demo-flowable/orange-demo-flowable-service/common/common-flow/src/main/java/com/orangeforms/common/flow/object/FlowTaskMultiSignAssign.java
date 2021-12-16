package com.orangeforms.common.flow.object;

import lombok.Data;

/**
 * 表示多实例任务的指派人信息。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowTaskMultiSignAssign {

    public static final String ASSIGN_TYPE_USER = "USER_GROUP";
    public static final String ASSIGN_TYPE_ROLE = "ROLE_GROUP";
    public static final String ASSIGN_TYPE_DEPT = "DEPT_GROUP";
    public static final String ASSIGN_TYPE_POST = "POST_GROUP";
    public static final String ASSIGN_TYPE_DEPT_POST = "DEPT_POST_GROUP";

    /**
     * 指派人类型。
     */
    private String assigneeType;
    /**
     * 逗号分隔的指派人列表。
     */
    private String assigneeList;
}
