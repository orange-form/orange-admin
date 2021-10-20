package com.flow.demo.common.flow.util;

import com.flow.demo.common.flow.listener.DeptPostLeaderListener;
import com.flow.demo.common.flow.listener.UpDeptPostLeaderListener;
import org.activiti.engine.delegate.TaskListener;

/**
 * 工作流与部门岗位相关的自定义扩展接口，需要业务模块自行实现该接口。也可以根据实际需求扩展该接口的方法。
 * 目前支持的主键类型为字符型和长整型，所以这里提供了两套实现接口。可根据实际情况实现其中一套即可。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface BaseFlowDeptPostExtHelper {

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
