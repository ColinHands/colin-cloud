package com.itmuch.contentcenter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.*;

/**
 * 这个类后面给删除掉了
 * 因为token直接在网关验证了
 *
 * 4.10节删除
 */
//@Configurable
public class Oauth2WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 指定验证token的认证服务器的地址
     * @return
     */
    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setClientId("orderService");
        tokenServices.setClientSecret("123456");
        tokenServices.setCheckTokenEndpointUrl("http://localhost:9000/oauth/check_token");
        // 作用是把用户传的token转成用户信息
        // 这样就可以在方法参数列表里 写 @AuthenticationPrincipal User username
        tokenServices.setAccessTokenConverter(getAccessTokenConverter());
        return tokenServices;
    }

    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    private AccessTokenConverter getAccessTokenConverter() {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter();
        userAuthenticationConverter.setUserDetailsService(userDetailsServiceImpl);
        // 作用是把根据用户名创建用户信息
        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
        return accessTokenConverter;
    }

    /**
     * 创建AuthenticationManager 用来认证请求中的token 然后再用RemoteTokenServices给认证服务器发送验证token请求
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        OAuth2AuthenticationManager auth2AuthenticationManager = new OAuth2AuthenticationManager();
        auth2AuthenticationManager.setTokenServices(tokenServices());
        return auth2AuthenticationManager;
    }
}
