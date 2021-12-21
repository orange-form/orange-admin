package com.orangeforms.uaaauth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orangeforms.uaaauth.dao.SysUaaUserMapper;
import com.orangeforms.uaaauth.model.SysUaaUser;
import com.orangeforms.common.core.base.service.BaseService;
import com.orangeforms.common.core.base.dao.BaseDaoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户管理数据操作服务类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Service
public class SysUaaUserService extends BaseService<SysUaaUser, Long> {

    @Autowired
    private SysUaaUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysUaaUser> mapper() {
        return sysUserMapper;
    }

    public SysUaaUser getByUsername(String username) {
        SysUaaUser filter = new SysUaaUser();
        filter.setUsername(username);
        return sysUserMapper.selectOne(new QueryWrapper<>(filter));
    }

    /**
     * 修改用户密码。
     * @param userId  用户主键Id。
     * @param newPass 新密码。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String newPass) {
        SysUaaUser updatedUser = new SysUaaUser();
        updatedUser.setUserId(userId);
        updatedUser.setPassword(passwordEncoder.encode(newPass));
        return sysUserMapper.updateById(updatedUser) == 1;
    }
}
