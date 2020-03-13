/**
 * 
 */
package com.imooc.security.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.imooc.security.core.properties.OAuth2ClientProperties;
import com.imooc.security.core.properties.CSecurityProperties;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import javax.sql.DataSource;

/**
 * 认证服务器配置
 * 
 * @author zhailiang
 *
 */
//@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 3600)
@Configuration
@EnableAuthorizationServer
public class ImoocAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	// 如果不继承AuthorizationServerConfigurerAdapter类 容器可以自动找到这些类并注入 否则必须自己实现
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenStore redisTokenStore;

	@Autowired(required = false)
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired(required = false)
	private TokenEnhancer jwtTokenEnhancer;

	@Autowired
	private CSecurityProperties CSecurityProperties;

	@Autowired
	private DataSource dataSource;

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

	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 认证及token配置
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(redisTokenStore)
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService);

		if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
			TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
			List<TokenEnhancer> enhancers = new ArrayList<>();
			enhancers.add(jwtTokenEnhancer);
			enhancers.add(jwtAccessTokenConverter);
			enhancerChain.setTokenEnhancers(enhancers);
			endpoints.tokenEnhancer(enhancerChain).accessTokenConverter(jwtAccessTokenConverter);
		}

	}

	/**
	 * tokenKey的访问权限表达式配置
	 */
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.passwordEncoder(passwordEncoder());
//		security.tokenKeyAccess("permitAll()");
		// 校验token的请求是需要经过身份认证的 例如 账号：orderApp 密码：123456
		security.tokenKeyAccess("isAuthenticated()") // 只有认证的请求才能通过请求拿到TokenKey 也就是jwt的密钥 拿着这个密钥去验签名
				.checkTokenAccess("isAuthenticated()");
	}

	/**
	 * 客户端配置
	 *
	 * 现在配置文件里的 security.oauth2.clients.clientId
	 * 不起作用了 起作用的是代码里配的 是有作用的 只是重写了
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource);
//		InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
//		if (ArrayUtils.isNotEmpty(CSecurityProperties.getOauth2().getClients())) {
//			for (OAuth2ClientProperties client : CSecurityProperties.getOauth2().getClients()) {
//				builder.withClient(client.getClientId())
//						.secret(client.getClientSecret())
//						// 对于现在的客户端（imooc）所支持的授权方式是什么
//						.authorizedGrantTypes("refresh_token", "authorization_code", "password")
//						.accessTokenValiditySeconds(client.getAccessTokenValidateSeconds())
//						.refreshTokenValiditySeconds(2592000)
//						.scopes("all");
//			}
//		}
	}

}
