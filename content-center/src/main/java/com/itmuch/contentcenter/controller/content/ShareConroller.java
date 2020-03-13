package com.itmuch.contentcenter.controller.content;

import com.itmuch.contentcenter.auth.CheckLogin;
import com.itmuch.contentcenter.auth.IsAuthorization;
import com.itmuch.contentcenter.domain.dto.content.ShareDTO;
import com.itmuch.contentcenter.feignclient.AuthCenterFeignClient;
import com.itmuch.contentcenter.service.content.ShareService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareConroller {
    private final ShareService shareService;

    private final AuthCenterFeignClient authCenterFeignClient;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ShareDTO findById(@PathVariable Integer id, @AuthenticationPrincipal String username) {
        return this.shareService.findById(id);
    }

    @GetMapping("/asd/{id}")
    @PreAuthorize("#oauth2.hasScope('all')")
    public ShareDTO findById2(@PathVariable Integer id, @AuthenticationPrincipal String username) {
        return this.shareService.findById(id);
    }

    @GetMapping("/test/{id}")
    @IsAuthorization
    public ShareDTO findById3(@PathVariable Integer id, @AuthenticationPrincipal String username) {
        String  s = authCenterFeignClient.getAdminInfo(username);
        return this.shareService.findById(id);
    }

    public Jws<Claims> getJwt(String jwtToken) {
        if (jwtToken.startsWith("bearer ")) {
            jwtToken = StringUtils.substring(jwtToken, "bearer ".length());
        }
        return Jwts.parser()  //得到DefaultJwtParser
                .setSigningKey("imooc") //设置签名的秘钥
                .parseClaimsJws(jwtToken);
    }

    @GetMapping("/test1/{id}")
    @CheckLogin
    public ShareDTO findById31(@PathVariable Integer id, @AuthenticationPrincipal String username) {
        String  s = authCenterFeignClient.getAdminInfo1(username);
        return this.shareService.findById(id);
    }
}
