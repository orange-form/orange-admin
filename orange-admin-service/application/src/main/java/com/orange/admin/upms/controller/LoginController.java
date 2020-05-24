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
import com.orange.admin.common.core.util.RsaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 登录接口控制器类。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
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
    public ResponseResult<JSONObject> doLogin(
            @RequestParam String loginName, @RequestParam String password) throws Exception {
        if (MyCommonUtil.existBlankArgument(loginName, password)) {
            return ResponseResult.error(ErrorCodeEnum.ARGUMENT_NULL_EXIST);
        }
        //NOTE: 执行RsaUtil工具类中的main函数，可以生成新的公钥和私钥。
        String privateKey =
                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKkLhAydtOtA4WuIkkIIUVaGWu4ElOEAQF9GTulHHWOwCHI1UvcKolvS1G+mdsKcmGtEAQ92AUde/kDRGu8Wn7kLDtCgUfo72soHz7Qfv5pVB4ohMxQd/9cxeKjKbDoirhB9Z3xGF20zUozp4ZPLxpTtI7azr0xzUtd5+D/HfLDrAgMBAAECgYEApESZhDz4YyeAJiPnpJ06lS8oS2VOWzsIUs0av5uoloeoHXtt7Lx7u2kroHeNrl3Hy2yg7ypH4dgQkGHin3VHrVAgjG3TxhgBXIqqntzzk2AGJKBeIIkRX86uTvtKZyp3flUgcwcGmpepAHS1V1DPY3aVYvbcqAmoL6DX6VYN0NECQQDQUitMdC76lEtAr5/ywS0nrZJDo6U7eQ7ywx/eiJ+YmrSye8oorlAj1VBWG+Cl6jdHOHtTQyYv/tu71fjzQiJTAkEAz7wb47/vcSUpNWQxItFpXz0o6rbJh71xmShn1AKP7XptOVZGlW9QRYEzHabV9m/DHqI00cMGhHrWZAhCiTkUCQJAFsJjaJ7o4weAkTieyO7B+CvGZw1h5/V55Jvcx3s1tH5yb22G0Jr6tm9/r2isSnQkReutzZLwgR3e886UvD7lcQJAAUcD2OOuQkDbPwPNtYwaHMbQgJj9JkOI9kskUE5vuiMdltOr/XFAyhygRtdmy2wmhAK1VnDfkmL6/IR8fEGImQJABOB0KCalb0M8CPnqqHzozrD8gPObnIIr4aVvLIPATN2g7MM2N6F7JbI4RZFiKa92LV6bhQCY8OvHi5K2cgFpbw==";
        SysUser user = sysUserService.getSysUserByLoginName(loginName);
        password = URLDecoder.decode(password, StandardCharsets.UTF_8.name());
        password = RsaUtil.decrypt(password, privateKey);
        if (user == null
                || !user.getPassword().equals(MyCommonUtil.encrptedPassword(password, appConfig.getPasswordSalt()))) {
            return ResponseResult.error(ErrorCodeEnum.INVALID_USERNAME_PASSWORD);
        }
        String errorMessage;
        if (user.getUserStatus() == SysUserStatus.STATUS_LOCKED) {
            errorMessage = "登录失败，用户账号被锁定！";
            return ResponseResult.error(ErrorCodeEnum.INVALID_USER_STATUS, errorMessage);
        }
        boolean isAdmin = user.getUserType() == SysUserType.TYPE_ADMIN;
        Map<String, Object> claims = new HashMap<>(3);
        String sessionId = MyCommonUtil.generateUuid();
        claims.put("sessionId", sessionId);
        String token = JwtUtil.generateToken(claims, appConfig.getTokenSigningKey());
        JSONObject jsonData = new JSONObject();
        jsonData.put(TokenData.REQUEST_ATTRIBUTE_NAME, token);
        jsonData.put("showName", user.getShowName());
        jsonData.put("isAdmin", isAdmin);
        TokenData tokenData = new TokenData();
        tokenData.setSessionId(sessionId);
        tokenData.setUserId(user.getUserId());
        tokenData.setDeptId(user.getDeptId());
        tokenData.setShowName(user.getShowName());
        tokenData.setIsAdmin(isAdmin);
        Cache sessionCache = cacheManager.getCache(CacheConfig.CacheEnum.GLOBAL_CACHE.name());
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
        return ResponseResult.success(jsonData);
    }

    /**
     * 登出操作。同时将Session相关的信息从缓存中删除。
     *
     * @return 应答结果对象。
     */
    @PostMapping("/doLogout")
    public ResponseResult<Void> doLogout() {
        TokenData tokenData = TokenData.takeFromRequest();
        sysPermService.removeUserSysPermCache(tokenData.getSessionId());
        sysDataPermService.removeDataPermCache(tokenData.getSessionId());
        return ResponseResult.success();
    }
}
