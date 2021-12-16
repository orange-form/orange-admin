package com.orangeforms.common.flow.custom;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserGroupManager implements UserGroupManager {

    @Override
    public List<String> getUserGroups(String username) {
        return null;
    }

    @Override
    public List<String> getUserRoles(String username) {
        return null;
    }

    @Override
    public List<String> getGroups() {
        return null;
    }

    @Override
    public List<String> getUsers() {
        return null;
    }
}
