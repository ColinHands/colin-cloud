package com.itmuch.gateway.feignclient.fallbackfactory;

import com.itmuch.gateway.feignclient.AuthCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@Slf4j
public class AuthCenterFeignClientFallbackFactory implements FallbackFactory<AuthCenterFeignClient> {
    @Override
    public AuthCenterFeignClient create(Throwable cause) {
        return new AuthCenterFeignClient() {
            @Override
            public String auth() {
                return "流控/降级返回的用户";
            }

            @Override
            public String getUser() {
                return "colin";
            }

            @Override
            public String getIgnoreUri(@RequestHeader(value = "Authorization", required = false) String token) {
                return null;
            }
        };
    }
}
