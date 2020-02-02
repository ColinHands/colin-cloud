package com.itmuch.gateway.feignclient;

import com.itmuch.gateway.feignclient.fallback.AuthCenterFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "auth-server", fallback = AuthCenterFeignClientFallback.class)
public interface AuthCenterFeignClient {
    @GetMapping("/user/auth")
    String auth();

    @GetMapping("/user/getUser")
    @ResponseBody
    String getUser();
}
