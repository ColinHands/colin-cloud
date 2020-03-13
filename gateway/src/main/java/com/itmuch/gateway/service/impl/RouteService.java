package com.itmuch.gateway.service.impl;

//import com.alicp.jetcache.Cache;
//import com.alicp.jetcache.anno.CacheType;
//import com.alicp.jetcache.anno.CreateCache;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.itmuch.gateway.feignclient.AuthCenterFeignClient;
import com.itmuch.gateway.service.IRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RouteService implements IRouteService {

    private static final String GATEWAY_ROUTES = "gateway_routes::";

    @Autowired
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Lazy
    private final AuthCenterFeignClient authCenterFeignClient;

//    @CreateCache(name = GATEWAY_ROUTES, cacheType = CacheType.REMOTE)
//    private Cache<String, RouteDefinition> gatewayRouteCache;

    private Map<String, RouteDefinition> routeDefinitionMaps = new HashMap<>();

    @PostConstruct
    private void loadRouteDefinition() {
        log.info("loadRouteDefinition, 开始初使化路由");
        Set<String> gatewayKeys = stringRedisTemplate.keys(GATEWAY_ROUTES + "*");
        if (CollectionUtils.isEmpty(gatewayKeys)) {
            return;
        }
        log.info("预计初使化路由, gatewayKeys：{}", gatewayKeys);
        // 去掉key的前缀
        Set<String> gatewayKeyIds = gatewayKeys.stream().map(key -> {
            return key.replace(GATEWAY_ROUTES, StringUtils.EMPTY);
        }).collect(Collectors.toSet());
//        Map<String, RouteDefinition> allRoutes = gatewayRouteCache.getAll(gatewayKeyIds);
        Map<String, RouteDefinition> allRoutes = Maps.newHashMap();
        log.info("gatewayKeys：{}", allRoutes);
        // 以下代码原因是，jetcache将RouteDefinition返序列化后，uri发生变化，未初使化，导致路由异常，以下代码是重新初使化uri
        allRoutes.values().forEach(routeDefinition -> {
            try {
                routeDefinition.setUri(new URI(routeDefinition.getUri().toASCIIString()));
            } catch (URISyntaxException e) {
                log.error("网关加载RouteDefinition异常：", e);
            }
        });
        routeDefinitionMaps.putAll(allRoutes);
        log.info("共初使化路由信息：{}", routeDefinitionMaps.size());
    }

    @Override
    public Collection<RouteDefinition> getRouteDefinitions() {
        return routeDefinitionMaps.values();
    }

    @Override
    public boolean save(RouteDefinition routeDefinition) {
        routeDefinitionMaps.put(routeDefinition.getId(), routeDefinition);
        log.info("新增路由1条：{},目前路由共{}条", routeDefinition, routeDefinitionMaps.size());
        return true;
    }

    @Override
    public boolean delete(String routeId) {
        routeDefinitionMaps.remove(routeId);
        log.info("删除路由1条：{},目前路由共{}条", routeId, routeDefinitionMaps.size());
        return true;
    }

    @Override
    public boolean ignoreAuthentication(String url, String token) {
        String ignoreUri = authCenterFeignClient.getIgnoreUri(token);
        String[] ignoreUris = ignoreUri.split(",");
        boolean isIgnore = false;

        AntPathMatcher antPathRequestMatcher = new AntPathMatcher(File.separator);
        for (String ignore : ignoreUris) {
            if (antPathRequestMatcher.match(ignore, url)) {
                isIgnore = true;
                break;
            }
        }
        return isIgnore;
    }

    @Value("${spring.security.oauth2.jwt.colin.keyUri}")
    private String keyUri;
    @Value("${spring.security.oauth2.jwt.colin.clientId}")
    private String clientId;
    @Value("${spring.security.oauth2.jwt.colin.clientSecret}")
    private String clientSecret;

    public String getTokenKey() {
        HttpHeaders headers = new HttpHeaders();

        RestTemplate template = new RestTemplateBuilder()
                .basicAuthentication(clientId, clientSecret)
                .build();

        ResponseEntity<String> responseEntity = template.exchange(
                keyUri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        JSONObject jsonObj = JSON.parseObject(responseEntity.getBody());
        return (String) jsonObj.get("value");
    }
}
