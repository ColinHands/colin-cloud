package com.itmuch.contentcenter.feignclient.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class TokenRelayRequestIntecepor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        // 1. 获取到token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("X-Token");
        String authorizationToken = request.getHeader("Authorization");

        // 2. 将token传递
        if (StringUtils.isNotBlank(authorizationToken)) {
            template.header("X-Token", token);
            template.header("Authorization", authorizationToken);
        }

    }
}
