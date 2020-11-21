package com.orange.demo.upmsservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import com.orange.demo.common.core.annotation.MyRequestBody;
import com.orange.demo.common.core.constant.ErrorCodeEnum;
import com.orange.demo.common.core.constant.ApplicationConstant;
import com.orange.demo.common.core.object.ResponseResult;
import com.orange.demo.common.core.object.TokenData;
import com.orange.demo.common.core.util.MyCommonUtil;
import com.orange.demo.common.core.util.RsaUtil;
import com.orange.demo.common.redis.cache.SessionCacheHelper;
import com.orange.demo.upmsinterface.constant.SysUserStatus;
import com.orange.demo.upmsinterface.constant.SysUserType;
import com.orange.demo.upmsservice.config.ApplicationConfig;
import com.orange.demo.upmsservice.model.SysUser;
import com.orange.demo.upmsservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 登录接口控制器类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@ApiSupport(order = 1)
@Api(tags = "登录接口")
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ApplicationConfig appConfig;
    @Autowired
    private SessionCacheHelper cacheHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 登录接口。
     *
     * @param loginName 登录名。
     * @param password  密码。
     * @return 应答结果对象，其中包括JWT的Token数据，以及菜单列表。
     */
    @ApiImplicitParams({
            // 这里包含密码密文，仅用于方便开发期间的接口测试，集成测试和发布阶段，需要将当前注解去掉。
            // 如果您重新生成了公钥和私钥，请替换password的缺省值。
            @ApiImplicitParam(name = "loginName", defaultValue = "admin"),
            @ApiImplicitParam(name = "password", defaultValue = "IP3ccke3GhH45iGHB5qP9p7iZw6xUyj28Ju10rnBiPKOI35sc%2BjI7%2FdsjOkHWMfUwGYGfz8ik31HC2Ruk%2Fhkd9f6RPULTHj7VpFdNdde2P9M4mQQnFBAiPM7VT9iW3RyCtPlJexQ3nAiA09OqG%2F0sIf1kcyveSrulxembARDbDo%3D")
    })
    @PostMapping("/doLogin")
    public ResponseResult<JSONObject> doLogin(
            @MyRequestBody String loginName, @MyRequestBody String password) throws Exception {
        if (MyCommonUtil.existBlankArgument(loginName, password)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        SysUser user = sysUserService.getSysUserByLoginName(loginName);
        password = URLDecoder.decode(password, StandardCharsets.UTF_8.name());
        // NOTE: 第一次使用时，请务必阅读ApplicationConstant.PRIVATE_KEY的代码注释。
        // 执行RsaUtil工具类中的main函数，可以生成新的公钥和私钥。
        password = RsaUtil.decrypt(password, ApplicationConstant.PRIVATE_KEY);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_USERNAME_PASSWORD);
        }
        String errorMessage;
        if (user.getUserStatus() == SysUserStatus.STATUS_LOCKED) {
            errorMessage = "登录失败，用户账号被锁定！";
            return ResponseResult.error(ErrorCodeEnum.INVALID_USER_STATUS, errorMessage);
        }
        JSONObject jsonData = this.buildLoginData(user);
        return ResponseResult.success(jsonData);
    }

    /**
     * 登出操作。同时将Session相关的信息从缓存中删除。
     *
     * @return 应答结果对象。
     */
    @PostMapping("/doLogout")
    public ResponseResult<Void> doLogout() {
        cacheHelper.removeAllSessionCache();
        return ResponseResult.success();
    }

    /**
     * 用户修改自己的密码。
     *
     * @param oldPass 原有密码。
     * @param newPass 新密码。
     * @return 应答结果对象。
     */
    @PostMapping("/changePassword")
    public ResponseResult<Void> changePassword(
            @MyRequestBody String oldPass, @MyRequestBody String newPass) throws Exception {
        if (MyCommonUtil.existBlankArgument(oldPass, oldPass)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        TokenData tokenData = TokenData.takeFromRequest();
        SysUser user = sysUserService.getById(tokenData.getUserId());
        oldPass = URLDecoder.decode(oldPass, StandardCharsets.UTF_8.name());
        // NOTE: 第一次使用时，请务必阅读ApplicationConstant.PRIVATE_KEY的代码注释。
        // 执行RsaUtil工具类中的main函数，可以生成新的公钥和私钥。
        oldPass = RsaUtil.decrypt(oldPass, ApplicationConstant.PRIVATE_KEY);
        if (user == null || !passwordEncoder.matches(oldPass, user.getPassword())) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_USERNAME_PASSWORD);
        }
        newPass = URLDecoder.decode(newPass, StandardCharsets.UTF_8.name());
        newPass = RsaUtil.decrypt(newPass, ApplicationConstant.PRIVATE_KEY);
        if (!sysUserService.changePassword(tokenData.getUserId(), newPass)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    private JSONObject buildLoginData(SysUser user) {
        boolean isAdmin = user.getUserType() == SysUserType.TYPE_ADMIN;
        TokenData tokenData = new TokenData();
        String sessionId = MyCommonUtil.generateUuid();
        tokenData.setUserId(user.getUserId());
        tokenData.setIsAdmin(isAdmin);
        tokenData.setShowName(user.getShowName());
        tokenData.setSessionId(sessionId);
        JSONObject jsonData = new JSONObject();
        jsonData.put(TokenData.REQUEST_ATTRIBUTE_NAME, tokenData);
        jsonData.put("showName", user.getShowName());
        jsonData.put("isAdmin", isAdmin);
        return jsonData;
    }
}
