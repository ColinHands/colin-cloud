package com.itmuch.contentcenter.feignclient;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.itmuch.contentcenter.domain.dto.user.UserDTO;
import com.itmuch.contentcenter.feignclient.fallbackfactory.AuthCenterFeignClientFallbackFactory;
import com.itmuch.contentcenter.feignclient.fallbackfactory.UserCenterFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

//@FeignClient(name = "user-center", configuration = GlobalFeignConfiguration.class)
@FeignClient(name = "auth-server",
//    fallback = UserCenterFeignClientFallback.class,
    fallbackFactory = AuthCenterFeignClientFallbackFactory.class
)
public interface AuthCenterFeignClient {
    /**
     * http://user-center/users/{id}
     *
     * @param serviceName
     * @return
     */
    @GetMapping("/user/get")
    List<Instance> findById(@RequestParam String serviceName);

    @GetMapping("/user/getUser")
    @ResponseBody
    String getUser();
}
