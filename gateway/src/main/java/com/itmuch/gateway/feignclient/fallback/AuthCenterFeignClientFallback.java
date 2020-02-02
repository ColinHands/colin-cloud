package com.itmuch.gateway.feignclient.fallback;

import com.itmuch.gateway.feignclient.AuthCenterFeignClient;
import org.springframework.stereotype.Component;

@Component
public class AuthCenterFeignClientFallback implements AuthCenterFeignClient {
    @Override
    public String auth() {
        return "流控/降级返回的用户";
    }

    @Override
    public String getUser() {
        return "colin";
    }
}
