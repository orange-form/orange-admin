package com.orange.admin.upms.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.orange.admin.config.ApplicationConfig;
import com.orange.admin.config.CacheConfig;
import com.orange.admin.upms.service.*;
import com.orange.admin.upms.model.SysMenu;
import com.orange.admin.upms.model.SysUser;
import com.orange.admin.upms.model.constant.SysUserStatus;
import com.orange.admin.upms.model.constant.SysUserType;
import com.orange.admin.common.core.annotation.NoAuthInterface;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.object.ResponseResult;
import com.orange.admin.common.core.object.TokenData;
import com.orange.admin.common.core.util.JwtUtil;
import com.orange.admin.common.core.util.MyCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录接口控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/login")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysPermCodeService sysPermCodeService;
    @Autowired
    private SysPermService sysPermService;
    @Autowired
    private SysDataPermService sysDataPermService;
    @Autowired
    private ApplicationConfig appConfig;
    @Autowired
    private CacheManager cacheManager;

    /**
     * 登录接口。
     *
     * @param loginName 登录名。
     * @param password  密码。
     * @return 应答结果对象，其中包括JWT的Token数据，以及菜单列表。
     */
    @NoAuthInterface
    @GetMapping("/doLogin")
    public ResponseResult<?> doLogin(@RequestParam String loginName, @RequestParam String password) {
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.NO_ERROR;
        String errorMessage = null;
        JSONObject jsonData = new JSONObject();
        do {
            if (MyCommonUtil.existBlankArgument(loginName, password)) {
                errorCodeEnum = ErrorCodeEnum.ARGUMENT_NULL_EXIST;
                break;
            }
            SysUser user = sysUserService.getSysUserByLoginName(loginName);
            if (user == null
                    || !user.getPassword().equals(MyCommonUtil.encrptedPassword(password, appConfig.getPasswordSalt()))) {
                errorCodeEnum = ErrorCodeEnum.INVALID_USERNAME_PASSWORD;
                break;
            }
            if (user.getUserStatus() == SysUserStatus.STATUS_LOCKED) {
                errorCodeEnum = ErrorCodeEnum.INVALID_USER_STATUS;
                errorMessage = "登录失败，用户账号被锁定！";
                break;
            }
            boolean isAdmin = user.getUserType() == SysUserType.TYPE_ADMIN;
            Map<String, Object> claims = new HashMap<>(3);
            String sessionId = MyCommonUtil.generateUuid();
            claims.put("sessionId", sessionId);
            String token = JwtUtil.generateToken(claims, appConfig.getTokenSigningKey());
            jsonData.put(TokenData.REQUEST_ATTRIBUTE_NAME, token);
            jsonData.put("showName", user.getShowName());
            jsonData.put("isAdmin", isAdmin);
            TokenData tokenData = new TokenData();
            tokenData.setSessionId(sessionId);
            tokenData.setUserId(user.getUserId());
            tokenData.setDeptId(user.getDeptId());
            tokenData.setShowName(user.getShowName());
            tokenData.setIsAdmin(isAdmin);
            Cache sessionCache = cacheManager.getCache(CacheConfig.CacheEnum.GlobalCache.name());
            sessionCache.put(sessionId, tokenData);
            List<SysMenu> menuList;
            if (isAdmin) {
                menuList = sysMenuService.getAllMenuList();
            } else {
                menuList = sysMenuService.getMenuListByUserId(user.getUserId());
                List<String> permCodeList = sysPermCodeService.getPermCodeListByUserId(user.getUserId());
                jsonData.put("permCodeList", permCodeList);
            }
            jsonData.put("menuList", menuList);
            if (user.getUserType() != SysUserType.TYPE_ADMIN) {
                // 缓存用户的权限资源
                sysPermService.putUserSysPermCache(sessionId, user.getUserId(), isAdmin);
                sysDataPermService.putDataPermCache(sessionId, user.getUserId(), user.getDeptId(), isAdmin);
            }
        } while (false);
        return ResponseResult.create(errorCodeEnum, errorMessage, jsonData);
    }

    /**
     * 登出操作。同时将Session相关的信息从缓存中删除。
     *
     * @return 应答结果对象。
     */
    @PostMapping("/doLogout")
    public ResponseResult<?> doLogout() {
        TokenData tokenData = TokenData.takeFromRequest();
        sysPermService.removeUserSysPermCache(tokenData.getSessionId());
        sysDataPermService.removeDataPermCache(tokenData.getSessionId());
        return ResponseResult.success();
    }
}
