/**
 *
 */
package com.imooc.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

import com.imooc.security.core.authorize.AuthorizeConfigProvider;

/**
 * @author colin cai
 *
 */
@Component
public class AuthAuthorizeConifgProvider implements AuthorizeConfigProvider {

    /* (non-Javadoc)
     * @see com.imooc.security.core.authorize.AuthorizeConfigProvider#config(org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry)
     */
    @Override
    public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
        config
                .antMatchers(HttpMethod.GET, "/fonts/**").permitAll()
                .antMatchers(HttpMethod.GET, "/user/me").access("hasRole('DDD')")
                .antMatchers(HttpMethod.GET,
                        "/**/*.html",
                        "/admin/me",
                        "/resource").authenticated()
                .anyRequest().authenticated();
//                .access("@accessService.hasPermission(request, authentication)");
        return true;
    }

}
