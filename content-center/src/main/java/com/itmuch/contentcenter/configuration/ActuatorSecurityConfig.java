/**
 * 
 */
package com.itmuch.contentcenter.configuration;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author jojo
 *
 * 因为启动类上加了 EnableResourceServer
 * 所以当前服务器会在所有访问此服务的请求的请求头里找token
 */
@Configuration
public class ActuatorSecurityConfig extends ResourceServerConfigurerAdapter {

	/**
	 * 这里就是控制请求访问此服务时的认证策略
	 * @param http
	 * @throws Exception
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
			.anyRequest().authenticated();
	}

	/**
	 * 标识这个资源服务器的服务ID
	 * 这一部分删除了 4.10节删除
	 * @param resources
	 * @throws Exception
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("content-center");
	}

	/**
	 * 这一部分删除了 4.10节删除
	 * 这里仅仅是为了证明在认证服务器那里配置的访问此服务的scope
	 * 在认证服务那里配置给访问此服务的客户发放的token只能有 read或者write权限
	 * 然后用户请求认证服务器获取token的时候可以指定scope 但必须是read和write
	 * http://localhost:9000/oauth/token?username=jj&password=123456&grant_type=password&scope=read write
	 * @param http
	 * @throws Exception
	 */
//	@Override
//	public void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//				.antMatchers(HttpMethod.POST).access("#oauth2.hasScope('write')")
//				.antMatchers(HttpMethod.GET).access("#oauth2.hasScope('read')");
//	}
}
