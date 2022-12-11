package com.imooc.security;

import com.imooc.security.core.authorize.AuthorizeHttpSecurityConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

/**
 * 核心模块的授权配置提供器，安全模块涉及的url的授权配置在这里。
 *
 * @author zhailiang
 */
@Component
@Order(Integer.MIN_VALUE)
public class DefaultAuthorizeHttpSecurityConfigProvider implements AuthorizeHttpSecurityConfigProvider {

    @Autowired
    HttpRedisCsrfTokenRepository httpRedisCsrfTokenRepository;

    @Override
    public boolean config(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(httpRedisCsrfTokenRepository);
        return false;
    }

}
