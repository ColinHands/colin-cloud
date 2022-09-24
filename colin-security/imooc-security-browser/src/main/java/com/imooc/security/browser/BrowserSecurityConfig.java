/**
 * 
 */
package com.imooc.security.browser;

import com.imooc.security.core.authentication.FormAuthenticationConfig;
import com.imooc.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.imooc.security.core.authorize.AuthorizeConfigManager;
import com.imooc.security.core.properties.CSecurityProperties;
import com.imooc.security.core.validate.code.ValidateCodeSecurityConfig;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Set;

/**
 * 浏览器环境下安全配置主类
 * 
 * @author zhailiang
 *
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CSecurityProperties CSecurityProperties;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
	
	@Autowired
	private ValidateCodeSecurityConfig validateCodeSecurityConfig;
	
	@Autowired
	private SpringSocialConfigurer imoocSocialSecurityConfig;
	
	@Autowired
	private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;
	
	@Autowired
	private InvalidSessionStrategy invalidSessionStrategy;
	
	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;
	
	@Resource
	private AuthorizeConfigManager authorizeConfigManager;
	
	@Autowired
	private FormAuthenticationConfig formAuthenticationConfig;
	
	@Autowired(required = false)
	private Set<BrowserSecurityConfigCallback> configCallbacks;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.GET, "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.gif", "/**/*.jpg");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if(CollectionUtils.isNotEmpty(configCallbacks)) {
			configCallbacks.forEach(callback -> callback.config(http));
		}
		
		formAuthenticationConfig.configure(http);

		// 校验码相关配置
		http.apply(validateCodeSecurityConfig)
				.and()
				// 短信登陆相关配置
			.apply(smsCodeAuthenticationSecurityConfig)
				.and()
				// Social相关配置 往当前的过滤器链上加上这个过滤器
			.apply(imoocSocialSecurityConfig)
				.and()
			// 记住我配置，如果想在'记住我'登录时记录日志，可以注册一个InteractiveAuthenticationSuccessEvent事件的监听器
			.rememberMe()
				.tokenRepository(persistentTokenRepository())
				.tokenValiditySeconds(CSecurityProperties.getBrowser().getRememberMeSeconds())
				.userDetailsService(userDetailsService)
				.and()
			.sessionManagement()
				// session 失效跳转的地址
//				.invalidSessionUrl("s")
				.invalidSessionStrategy(invalidSessionStrategy)
				// 用户的最大session并发数
				.maximumSessions(CSecurityProperties.getBrowser().getSession().getMaximumSessions())
				// 当session数量达到最大的数量后阻止后来的登陆行为
				.maxSessionsPreventsLogin(CSecurityProperties.getBrowser().getSession().isMaxSessionsPreventsLogin())
				// 并发登录导致的session失效处理器
				.expiredSessionStrategy(sessionInformationExpiredStrategy)
				.and()
				.and()
				// 退出操作
			.logout()
				.logoutUrl("/signOut")
				.logoutSuccessHandler(logoutSuccessHandler)
				.deleteCookies("JSESSIONID")
				.and()
			.csrf().disable()
			.headers().frameOptions().disable();
		
		authorizeConfigManager.config(http.authorizeRequests());
		
	}

	/**
	 * 记住我功能的token存取器配置
	 * @return
	 */
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
//		tokenRepository.setCreateTableOnStartup(true);
		return tokenRepository;
	}
	
}
