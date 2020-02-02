package com.itmuch.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

//@Component
public class OAuthSignatureFilter implements GlobalFilter, Ordered {
    /**授权访问用户名*/
    @Value("${spring.security.user.name}")
    private String securityUserName;
    /**授权访问密码*/
    @Value("${spring.security.user.password}")
    private String securityUserPassword;

    /**
     * OAuth过滤器
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * 作者:will
     * 日期:2019/4/4 13:36
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /**oauth授权*/
        String auth= securityUserName.concat(":").concat(securityUserPassword);
        String encodedAuth = new sun.misc.BASE64Encoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
        //注意Basic后面有空格
        String authHeader= "Basic " +encodedAuth;
        //向headers中放授权信息
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().header("Authorization",authHeader).build();
        //将现在的request变成change对象
        ServerWebExchange build =exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(build);
    }


    /**
     * 优先级
     * @return int 数字越大优先级越低
     * 作者:will
     * 日期:2019/4/4 13:36
     */
    @Override
    public int getOrder() {
        return 2;
    }
}
