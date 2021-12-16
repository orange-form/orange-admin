package com.orangeforms.common.flow.object;

import com.orangeforms.common.flow.constant.FlowConstant;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * 流程任务岗位候选组数据。仅用于流程任务的候选组类型为岗位时。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
public class FlowTaskPostCandidateGroup {

    /**
     * 唯一值，目前仅前端使用。
     */
    private String id;
    /**
     * 岗位类型。
     * 1. 所有部门岗位审批变量，值为 (allDeptPost)。
     * 2. 本部门岗位审批变量，值为 (selfDeptPost)。
     * 3. 上级部门岗位审批变量，值为 (upDeptPost)。
     * 4. 任意部门关联的岗位审批变量，值为 (deptPost)。
     */
    private String type;
    /**
     * 岗位Id。type为(1,2,3)时使用该值。
     */
    private String postId;
    /**
     * 部门岗位Id。type为(4)时使用该值。
     */
    private String deptPostId;

    public static List<String> buildCandidateGroupList(List<FlowTaskPostCandidateGroup> groupDataList) {
        List<String> candidateGroupList = new LinkedList<>();
        for (FlowTaskPostCandidateGroup groupData : groupDataList) {
            switch (groupData.getType()) {
                case FlowConstant.GROUP_TYPE_ALL_DEPT_POST_VAR:
                    candidateGroupList.add(groupData.getPostId());
                    break;
                case FlowConstant.GROUP_TYPE_DEPT_POST_VAR:
                    candidateGroupList.add(groupData.getDeptPostId());
                    break;
                case FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR:
                    candidateGroupList.add("${" + FlowConstant.SELF_DEPT_POST_PREFIX + groupData.getPostId() + "}");
                    break;
                case FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR:
                    candidateGroupList.add("${" + FlowConstant.UP_DEPT_POST_PREFIX + groupData.getPostId() + "}");
                    break;
                default:
                    break;
            }
        }
        return candidateGroupList;
    }
}
