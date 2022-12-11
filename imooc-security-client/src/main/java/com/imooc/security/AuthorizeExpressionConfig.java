package com.imooc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

//@Configuration
@Order(Integer.MIN_VALUE)
public class AuthorizeExpressionConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuth2WebSecurityExpressionHandler expressionHandler;

    @Autowired
    HttpRedisCsrfTokenRepository httpRedisCsrfTokenRepository;

    @Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
        OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }

    // 不加这个的话会报SpelEvaluationException的异常 @rbacService.hasPermission
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.expressionHandler(expressionHandler);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(httpRedisCsrfTokenRepository);
    }

    /**
     * @return
     */
    @Bean
//    @ConditionalOnBean(TokenEnhancer.class)
    public TokenEnhancer jwtTokenEnhancer() {
        return new TokenJwt1Enhancer();
    }
}
