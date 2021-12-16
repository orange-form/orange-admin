package com.orangeforms.webadmin.app.util;

import cn.hutool.core.collection.CollUtil;
import com.orangeforms.common.flow.util.BaseFlowIdentityExtHelper;
import com.orangeforms.common.flow.util.FlowCustomExtFactory;
import com.orangeforms.webadmin.upms.model.SysDept;
import com.orangeforms.webadmin.upms.model.SysUser;
import com.orangeforms.webadmin.upms.model.constant.SysUserStatus;
import com.orangeforms.webadmin.upms.model.SysDeptPost;
import com.orangeforms.webadmin.upms.service.SysDeptService;
import com.orangeforms.webadmin.upms.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 为流程提供所需的用户身份相关的等扩展信息的帮助类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Component
public class FlowIdentityExtHelper implements BaseFlowIdentityExtHelper {

    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    @PostConstruct
    public void doRegister() {
        flowCustomExtFactory.registerFlowIdentityExtHelper(this);
    }

    @Override
    public Long getLeaderDeptPostId(Long deptId) {
        List<Long> deptPostIdList = sysDeptService.getLeaderDeptPostIdList(deptId);
        return CollUtil.isEmpty(deptPostIdList) ? null : deptPostIdList.get(0);
    }

    @Override
    public Long getUpLeaderDeptPostId(Long deptId) {
        List<Long> deptPostIdList = sysDeptService.getUpLeaderDeptPostIdList(deptId);
        return CollUtil.isEmpty(deptPostIdList) ? null : deptPostIdList.get(0);
    }

    @Override
    public Map<String, String> getDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        Set<Long> postIdSet2 = postIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        List<SysDeptPost> deptPostList = sysDeptService.getSysDeptPostList(deptId, postIdSet2);
        if (CollUtil.isEmpty(deptPostList)) {
            return null;
        }
        Map<String, String> resultMap = new HashMap<>(deptPostList.size());
        deptPostList.forEach(sysDeptPost ->
                resultMap.put(sysDeptPost.getPostId().toString(), sysDeptPost.getDeptPostId().toString()));
        return resultMap;
    }

    @Override
    public Map<String, String> getUpDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        SysDept sysDept = sysDeptService.getById(deptId);
        if (sysDept == null || sysDept.getParentId() == null) {
            return null;
        }
        return getDeptPostIdMap(sysDept.getParentId(), postIdSet);
    }

    @Override
    public Set<String> getUsernameListByRoleIds(Set<String> roleIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> roleIdSet2 = roleIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long roleId : roleIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByRoleId(roleId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    @Override
    public Set<String> getUsernameListByDeptIds(Set<String> deptIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> deptIdSet2 = deptIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        for (Long deptId : deptIdSet2) {
            SysUser filter = new SysUser();
            filter.setDeptId(deptId);
            filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
            List<SysUser> userList = sysUserService.getSysUserList(filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    @Override
    public Set<String> getUsernameListByPostIds(Set<String> postIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> postIdSet2 = postIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long postId : postIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByPostId(postId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    @Override
    public Set<String> getUsernameListByDeptPostIds(Set<String> deptPostIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> deptPostIdSet2 = deptPostIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long deptPostId : deptPostIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByDeptPostId(deptPostId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    private void extractAndAppendUsernameList(Set<String> resultUsernameList, List<SysUser> userList) {
        List<String> usernameList = userList.stream().map(SysUser::getLoginName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(usernameList)) {
            resultUsernameList.addAll(usernameList);
        }
    }
}
