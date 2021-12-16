package com.orangeforms.webadmin.upms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.orangeforms.common.core.annotation.MyRequestBody;
import com.orangeforms.common.core.object.*;
import com.orangeforms.common.core.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 在线用户控制器对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@RequestMapping("/admin/upms/loginUser")
public class LoginUserController {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 显示在线用户列表。
     *
     * @param loginName 登录名过滤。
     * @param pageParam 分页参数。
     * @return 登录用户信息列表。
     */
    @PostMapping("/list")
    public ResponseResult<MyPageData<LoginUserInfo>> list(
            @MyRequestBody String loginName, @MyRequestBody MyPageParam pageParam) {
        int queryCount = pageParam.getPageNum() * pageParam.getPageSize();
        int skipCount = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        String patternKey;
        if (StrUtil.isBlank(loginName)) {
            patternKey = RedisKeyUtil.getSessionIdPrefix() + "*";
        } else {
            patternKey = RedisKeyUtil.getSessionIdPrefix(loginName) + "*";
        }
        List<LoginUserInfo> loginUserInfoList = new LinkedList<>();
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(patternKey);
        for (String key : keys) {
            loginUserInfoList.add(this.buildTokenDataByRedisKey(key));
        }
        loginUserInfoList.sort((o1, o2) -> (int) (o2.getLoginTime().getTime() - o1.getLoginTime().getTime()));
        int toIndex = Math.min(skipCount + pageParam.getPageSize(), loginUserInfoList.size());
        List<LoginUserInfo> resultList = loginUserInfoList.subList(skipCount, toIndex);
        return ResponseResult.success(new MyPageData<>(resultList, (long) loginUserInfoList.size()));
    }

    /**
     * 强制下线指定登录会话。
     *
     * @param sessionId 待强制下线的SessionId。
     * @return 应答结果对象。
     */
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@MyRequestBody String sessionId) {
        // 为了保证被剔除用户正在进行的操作不被干扰，这里只是删除sessionIdKey即可，这样可以使强制下线操作更加平滑。
        // 比如，如果删除操作权限或数据权限的redis session key，那么正在请求数据的操作就会报错。
        redissonClient.getBucket(RedisKeyUtil.makeSessionIdKey(sessionId)).delete();
        return ResponseResult.success();
    }

    private LoginUserInfo buildTokenDataByRedisKey(String key) {
        RBucket<String> sessionData = redissonClient.getBucket(key);
        TokenData tokenData = JSON.parseObject(sessionData.get(), TokenData.class);
        return BeanUtil.copyProperties(tokenData, LoginUserInfo.class);
    }
}
