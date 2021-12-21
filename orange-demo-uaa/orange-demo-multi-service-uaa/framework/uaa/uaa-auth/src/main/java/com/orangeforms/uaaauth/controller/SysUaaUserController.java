package com.orangeforms.uaaauth.controller;

import com.orangeforms.uaaauth.model.SysUaaUser;
import com.orangeforms.uaaauth.service.SysUaaUserService;
import com.orangeforms.common.core.constant.ErrorCodeEnum;
import com.orangeforms.common.core.object.ResponseResult;
import com.orangeforms.common.core.util.MyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * UaaUser用户对象查询示例接口。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@RestController
@RequestMapping("/uaaauth/sysUaaUser")
public class SysUaaUserController {

    @Autowired
    private SysUaaUserService sysUaaUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取UAA授权用户的相信信息。
     * 在Uaa客户端应用正确获取access_token后，可通过请求
     * /admin/uaa/sysUaaUser/view?username=xxx&access_token=yyy 获取用户的详情信息。
     *
     * @param username 用户名。
     * @return 授权用户的详细信息。
     */
    @GetMapping("/view")
    public ResponseResult<SysUaaUser> view(@RequestParam String username) {
        if (MyCommonUtil.existBlankArgument(username)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        // 这里查看用户数据时候，需要把用户多对多关联的角色和数据权限Id一并查出。
        SysUaaUser user = sysUaaUserService.getByUsername(username);
        if (user == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success(user);
    }

    /**
     * 操作员修改自己的密码。
     *
     * @param username 用户名。
     * @param oldPass  原有密码明文。
     * @param newPass  新密码明文。
     * @return 应答结果对象。
     */
    @GetMapping("/changePassword")
    public ResponseResult<Void> changePassword(
            @RequestParam String username, @RequestParam String oldPass, @RequestParam String newPass) {
        if (MyCommonUtil.existBlankArgument(newPass, oldPass)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysUaaUser user = sysUaaUserService.getByUsername(username);
        if (user == null || !passwordEncoder.matches(oldPass, user.getPassword())) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_USERNAME_PASSWORD);
        }
        if (!sysUaaUserService.changePassword(user.getUserId(), newPass)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }
}
