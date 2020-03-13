package com.itmuch.contentcenter.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;


@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAuthenticationAspect {

    @Autowired
    private HandlerMappingIntrospector mvcHandlerMappingIntrospector;

    @Around("@annotation(com.itmuch.contentcenter.auth.IsAuthorization)")
    public Object isAuthorization(ProceedingJoinPoint point) throws Throwable {
        isAuthorization();
        return point.proceed();
    }

    private void isAuthorization() {
        try {
            // 1. 从header里面获取token
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader("Authorization");

            token = new ObjectMapper().writeValueAsString(getJwt(token).getBody());

            JSONObject jsonObj = JSON.parseObject(token);
            Object o = jsonObj.get("urls");
            JSONArray array = (JSONArray) o;
            List<String> urls = Lists.newArrayList();
            for (Object value : array) {
                String sss = value.toString();
                if (sss.contains(",")) {
                    urls.addAll(Lists.newArrayList(sss.split(",")));
                } else {
                    urls.add(sss);
                }
            }

            boolean yes = false;
            for (String url : urls) {
                AntPathMatcher antPathRequestMatcher = new AntPathMatcher(File.separator);
                if (antPathRequestMatcher.match(url, request.getRequestURI())) {
                    yes = true;
                    break;
                }
            }

            if (!yes) {
                throw new SecurityException("无权限！");
            }
        } catch (Throwable throwable) {
            throw new SecurityException("Token不合法");
        }
    }

    public Jws<Claims> getJwt(String jwtToken) {
        if (jwtToken.startsWith("bearer ")) {
            jwtToken = StringUtils.substring(jwtToken, "bearer ".length());
        }
        String signingKey = "imooc";
        return Jwts.parser()  //得到DefaultJwtParser
                .setSigningKey(signingKey.getBytes()) //设置签名的秘钥
                .parseClaimsJws(jwtToken);
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getRequest();
    }

    private MvcRequestMatcher newMvcRequestMatcher(String url, String method) {
        return new NewMvcRequestMatcher(mvcHandlerMappingIntrospector, url, method);
    }

    public static void main(String[] args) {
        String s = "{\"com\":\"com\",\"urls\":[\"/role/**\",\"/auth/**\",\"roleManage,/force/**/s\"],\"role\":[\"forerunner\",\"超级管理员\"],\"user_name\":\"1111\",\"scope\":[\"read\"],\"exp\":1583683736,\"jti\":\"3a7d6c01-8e50-405c-b352-c15ca9fdea34\",\"client_id\":\"admin\"}";
        JSONObject jsonObj = JSON.parseObject(s);
        Object o = jsonObj.get("urls");
        JSONArray array = (JSONArray) o;
        List<String> urls = Lists.newArrayList();
        for (Object value : array) {
            String sss = value.toString();
            if (sss.contains(",")) {
                urls.addAll(Lists.newArrayList(sss.split(",")));
            } else {
                urls.add(sss);
            }
        }

        for (String url : urls) {
            AntPathMatcher antPathRequestMatcher = new AntPathMatcher(File.separator);
            if (antPathRequestMatcher.match(url, "/auth")) {
                System.err.println("pic ===>>> " + url);
            }
        }

    }
}
