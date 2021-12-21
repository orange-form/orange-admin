package com.orangeforms.uaaauth.config;

import com.orangeforms.common.core.constant.ApplicationConstant;
import com.orangeforms.common.core.util.RsaUtil;
import com.orangeforms.uaaauth.handler.AuthLogoutHandler;
import com.orangeforms.uaaauth.handler.AuthLogoutSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 认证授权服务安全配置类。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Slf4j
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 全局用户信息。
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new CustomPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                // login.html位于resources/static目录中，用户登录时，将使用该页面。
                .loginPage("/login.html")
                // 登录处理url。在login.html的表单提交中，将使用该uri。
                .loginProcessingUrl("/uaa/login")
                // 注册登录成功后的处理器。
                .successHandler(loginSuccessHandler())
                .and()
                .logout()
                // 登出时调用的uri，该uri的处理逻辑，已在oauth内置的filter中实现。
                .logoutUrl("/oauth/remove/token")
                // 登出成功处理器
                .logoutSuccessHandler(new AuthLogoutSuccessHandler())
                // 登出操作处理器。
                .addLogoutHandler(authLogoutHandler())
                // 登出后清除session。以使用户下次访问授权页面时，必须重新进行登录验证。
                .clearAuthentication(true)
                .and()
                .csrf().disable()
                // 解决不允许显示在iframe的问题
                .headers().frameOptions().disable().cacheControl();
        // 授权码模式单独处理，需要session的支持，此模式可以支持所有oauth2的认证
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    /**
     * 自定义的密码匹配对象。相比于Spring Security的BCryptPasswordEncoder，会现将前端传入
     * 的密码进行RSA解密，再将解密后的结果交由BCryptPasswordEncoder对象进行密码匹配。
     */
    static class CustomPasswordEncoder extends BCryptPasswordEncoder {
        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            try {
                rawPassword = URLDecoder.decode(rawPassword.toString(), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                log.error("Failed to call URLDecoder.decode for passwd [" + rawPassword + "]", e);
                return false;
            }
            try {
                rawPassword = RsaUtil.decrypt(rawPassword.toString(), ApplicationConstant.PRIVATE_KEY);
            } catch (Exception e) {
                log.error("Failed to call RsaUtil.decrypt for passwd [" + rawPassword + "]", e);
                return false;
            }
            return super.matches(rawPassword, encodedPassword);
        }
    }

    /**
     * 用户认证管理对象。
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 登出操作处理器。
     * @return 登出操作处理器。
     */
    @Bean
    public AuthLogoutHandler authLogoutHandler() {
        return new AuthLogoutHandler();
    }

    /**
     * 登录成功操作处理器。
     * @return 登录成功操作处理器。
     */
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
}
