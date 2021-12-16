package com.orangeforms.common.flow.util;

import com.orangeforms.common.flow.listener.DeptPostLeaderListener;
import com.orangeforms.common.flow.listener.UpDeptPostLeaderListener;
import org.activiti.engine.delegate.TaskListener;

import java.util.Map;
import java.util.Set;

/**
 * 工作流与用户身份相关的自定义扩展接口，需要业务模块自行实现该接口。也可以根据实际需求扩展该接口的方法。
 * 目前支持的主键类型为字符型和长整型，所以这里提供了两套实现接口。可根据实际情况实现其中一套即可。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface BaseFlowIdentityExtHelper {

    /**
     * 根据(字符型)部门Id，获取当前用户部门领导所有的部门岗位Id。
     *
     * @param deptId 用户所在部门Id。
     * @return 当前用户部门领导所有的部门岗位Id。
     */
    default String getLeaderDeptPostId(String deptId) {
        return null;
    }

    /**
     * 根据(字符型)部门Id，获取当前用户上级部门领导所有的部门岗位Id。
     *
     * @param deptId 用户所在部门Id。
     * @return 当前用户上级部门领导所有的部门岗位Id。
     */
    default String getUpLeaderDeptPostId(String deptId) {
        return null;
    }

    /**
     * 获取(字符型)指定部门上级部门的指定岗位集合的DeptPostId集合。
     *
     * @param deptId    指定的部门Id。
     * @param postIdSet 指定的岗位Id集合。
     * @return 与该部门Id上级部门关联的岗位Id集合，key对应参数中的postId，value是与key对应的deptPostId。
     */
    default Map<String, String> getUpDeptPostIdMap(String deptId, Set<String> postIdSet) {
        return null;
    }

    /**
     * 获取(字符型)指定部门的指定岗位集合的DeptPostId集合。
     *
     * @param deptId    指定的部门Id。
     * @param postIdSet 指定的岗位Id集合。
     * @return 与部门关联的岗位Id集合，key对应参数中的postId，value是与key对应的deptPostId。
     */
    default Map<String, String> getDeptPostIdMap(String deptId, Set<String> postIdSet) {
        return null;
    }

    /**
     * 根据(长整型)部门Id，获取当前用户部门领导所有的部门岗位Id。
     *
     * @param deptId 用户所在部门Id。
     * @return 当前用户部门领导所有的部门岗位Id。
     */
    default Long getLeaderDeptPostId(Long deptId) {
        return null;
    }

    /**
     * 根据(长整型)部门Id，获取当前用户上级部门领导所有的部门岗位Id。
     *
     * @param deptId 用户所在部门Id。
     * @return 当前用户上级部门领导所有的部门岗位Id。
     */
    default Long getUpLeaderDeptPostId(Long deptId) {
        return null;
    }

    /**
     * 获取(长整型)指定部门的指定岗位集合的DeptPostId集合。
     *
     * @param deptId    指定的部门Id。
     * @param postIdSet 指定的岗位Id集合。
     * @return 与部门关联的岗位Id集合，key对应参数中的postId，value是与key对应的deptPostId。
     */
    default Map<String, String> getDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        return null;
    }

    /**
     * 获取(长整型)指定部门上级部门的指定岗位集合的DeptPostId集合。
     *
     * @param deptId    指定的部门Id。
     * @param postIdSet 指定的岗位Id集合。
     * @return 与该部门Id上级部门关联的岗位Id集合，key对应参数中的postId，value是与key对应的deptPostId。
     */
    default Map<String, String> getUpDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        return null;
    }

    /**
     * 根据角色Id集合，查询所属的用户名列表。
     *
     * @param roleIdSet 角色Id集合。
     * @return 所属的用户列表。
     */
    default Set<String> getUsernameListByRoleIds(Set<String> roleIdSet) {
        return null;
    }

    /**
     * 根据部门Id集合，查询所属的用户名列表。
     *
     * @param deptIdSet 部门Id集合。
     * @return 所属的用户列表。
     */
    default Set<String> getUsernameListByDeptIds(Set<String> deptIdSet) {
        return null;
    }

    /**
     * 根据岗位Id集合，查询所属的用户名列表。
     *
     * @param postIdSet 岗位Id集合。
     * @return 所属的用户列表。
     */
    default Set<String> getUsernameListByPostIds(Set<String> postIdSet) {
        return null;
    }

    /**
     * 根据部门岗位Id集合，查询所属的用户名列表。
     *
     * @param deptPostIdSet 部门岗位Id集合。
     * @return 所属的用户列表。
     */
    default Set<String> getUsernameListByDeptPostIds(Set<String> deptPostIdSet) {
        return null;
    }

    /**
     * 获取任务执行人是当前部门领导岗位的任务监听器。
     * 通常会在没有找到领导部门岗位Id的时候，为当前任务指定其他的指派人、候选人或候选组。
     *
     * @return 任务监听器。
     */
    default Class<? extends TaskListener> getDeptPostLeaderListener() {
        return DeptPostLeaderListener.class;
    }

    /**
     * 获取任务执行人是上级部门领导岗位的任务监听器。
     * 通常会在没有找到领导部门岗位Id的时候，为当前任务指定其他的指派人、候选人或候选组。
     *
     * @return 任务监听器。
     */
    default Class<? extends TaskListener> getUpDeptPostLeaderListener() {
        return UpDeptPostLeaderListener.class;
    }
}
