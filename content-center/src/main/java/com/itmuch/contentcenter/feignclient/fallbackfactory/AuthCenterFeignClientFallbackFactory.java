package com.itmuch.contentcenter.feignclient.fallbackfactory;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.itmuch.contentcenter.domain.dto.user.UserDTO;
import com.itmuch.contentcenter.feignclient.AuthCenterFeignClient;
import com.itmuch.contentcenter.feignclient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@Slf4j
public class AuthCenterFeignClientFallbackFactory implements FallbackFactory<AuthCenterFeignClient> {
    @Override
    public AuthCenterFeignClient create(Throwable cause) {
        return new AuthCenterFeignClient() {
            @Override
            public String getUser() {
                log.warn("远程调用被限流/降级了", cause);
                UserDTO userDTO = new UserDTO();
                userDTO.setWxNickname("流控/降级返回的用户");
                return "Colin";
            }

            @Override
            public String getAdminInfo(String name) {
                return null;
            }

            @Override
            public String getAdminInfo1(String name) {
                return null;
            }

            @Override
            public List<Instance> findById(String serviceName) {
                return null;
            }
        };
    }
}
