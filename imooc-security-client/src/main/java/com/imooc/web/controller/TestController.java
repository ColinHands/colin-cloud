/**
 *
 */
package com.imooc.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.imooc.dto.AdminInfo;
import com.imooc.dto.User;
import com.imooc.dto.UserQueryCondition;
import com.imooc.security.app.social.AppSingUpUtils;
import com.imooc.security.core.properties.CSecurityProperties;
import com.imooc.service.AdminService;
import io.jsonwebtoken.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author colin
 *
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final ProviderSignInUtils providerSignInUtils;

    @Autowired
    private AppSingUpUtils appSingUpUtils;

    private final CSecurityProperties CSecurityProperties;

    private final AdminService adminService;

    @GetMapping("/test")
    @PermitAll
    @Retryable
    public void test(HttpServletRequest request) {
        String a = "";
    }

    @PostMapping("/csrf")
    @PermitAll
    public String csrf(HttpServletRequest request) {
        return "success";
    }


}
