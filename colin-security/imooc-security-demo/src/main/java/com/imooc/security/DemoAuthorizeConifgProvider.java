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
 * @author zhailiang
 *
 */
@Component
public class DemoAuthorizeConifgProvider implements AuthorizeConfigProvider {

	/* (non-Javadoc)
	 * @see com.imooc.security.core.authorize.AuthorizeConfigProvider#config(org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry)
	 */
	@Override
	public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
		config
				.antMatchers("/test/**").permitAll()
				.antMatchers("/user/regist").permitAll()
				.antMatchers("/user/register-admin").permitAll()
				.antMatchers(HttpMethod.GET, "/fonts/**").permitAll()
				.antMatchers(HttpMethod.GET, "/auth/**").permitAll()
				.antMatchers("/auth/**").permitAll()
				.antMatchers(HttpMethod.GET, "/user/me").permitAll()
//                .access("hasRole('DDD')")
				.antMatchers(HttpMethod.GET,
						"/**/*.html",
						"/admin/me",
						"/resource").authenticated()
				.anyRequest()
                .access("@rbacService.hasPermission(request, authentication)");
		return true;
	}

}
