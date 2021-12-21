package com.orangeforms.uaaauth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orangeforms.uaaauth.dao.SysUaaUserMapper;
import com.orangeforms.uaaauth.model.SysUaaUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * UAA授权用户数据操作服务类。该类必须为UserDetailsService接口的实现类。
 * loadUserByUsername方法，被Oauth2框架调用获取授权用户数据。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUaaUserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUaaUser filter = new SysUaaUser();
        filter.setUsername(username);
        SysUaaUser sysUaaUser = mapper.selectOne(new QueryWrapper<>(filter));
        if (sysUaaUser == null) {
            throw new UsernameNotFoundException("用户名不存在！[" +  username + "]");
        }
        return new User(
                sysUaaUser.getUsername(),
                sysUaaUser.getPassword(),
                true,
                true,
                true,
                !sysUaaUser.getLocked(),
                Collections.emptyList());
    }
}
