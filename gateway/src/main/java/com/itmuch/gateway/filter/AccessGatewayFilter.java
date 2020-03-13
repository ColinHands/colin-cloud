package com.itmuch.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmuch.gateway.feignclient.AuthCenterFeignClient;
import com.itmuch.gateway.service.IPermissionService;
import com.itmuch.gateway.service.IRouteService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 请求url权限校验
 */
@Configuration
//@ComponentScan(basePackages = "com.springboot.cloud.auth.client")
@Slf4j
public class AccessGatewayFilter implements GlobalFilter {

    private static final String X_CLIENT_TOKEN_USER = "x-client-token-user";
    private static final String X_CLIENT_TOKEN = "x-client-token";

    /**
     * 由authentication-client模块提供签权的feign客户端
     */
    @Resource
    private IRouteService routeService;

    @Resource
    private IPermissionService permissionService;

    @Resource
    private AuthCenterFeignClient authCenterFeignClient;

    /**
     * 1.首先网关检查token是否有效，无效直接返回401，不调用签权服务
     * 2.调用签权服务器看是否对该请求有权限，有权限进入下一个filter，没有权限返回401
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authentication = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String method = request.getMethodValue();
        String url = request.getPath().value();
        log.debug("url:{},method:{},headers:{}", url, method, request.getHeaders());
        // 不需要网关签权的url
        if (routeService.ignoreAuthentication(url, authentication)) {
            return chain.filter(exchange);
        }

        // 调用签权服务看用户是否有权限，若有权限进入下一个filter
        if (!checkToken(authentication)) {
            return unauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    /**
     * 提取jwt token中的数据，转为json
     *
     * @param authentication
     * @return
     */
    private boolean checkToken(String authentication) {
        try {
            String token = new ObjectMapper().writeValueAsString(getJwt(authentication).getBody());
            return token != null;
        } catch (JsonProcessingException e) {
            log.error("token json error:{}", e.getMessage());
        }
        return false;
    }

    private static final String BEARER = "bearer ";

    public Jws<Claims> getJwt(String jwtToken) {
        if (jwtToken.startsWith(BEARER)) {
            jwtToken = StringUtils.substring(jwtToken, BEARER.length());
        }

        String signingKey = routeService.getTokenKey();
        return Jwts.parser() // 得到DefaultJwtParser
                .setSigningKey(signingKey.getBytes()) // 设置签名的秘钥
                .parseClaimsJws(jwtToken);
    }

    /**
     * 网关拒绝，返回401
     *
     * @param
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }
}
