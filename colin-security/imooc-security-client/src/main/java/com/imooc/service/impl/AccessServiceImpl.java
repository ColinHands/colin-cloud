/**
 * 
 */
package com.imooc.service.impl;

import com.imooc.dto.AdminInfo;
import com.imooc.entity.Admin;
import com.imooc.service.AccessService;
import com.imooc.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author colin cai
 *
 */
@Component("accessService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class AccessServiceImpl implements AccessService {

	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	private final AdminService adminService;

	@Override
	public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
		Object principal = authentication.getPrincipal();

		boolean hasPermission = false;
		if (principal.equals("anonymousUser")) {
			hasPermission = true;
		}

		if (principal instanceof String) {
			AdminInfo adminInfo = adminService.getInfo((String) principal);
			if (adminInfo != null && adminInfo.getId() != null) {
				hasPermission = true;
			}
		}

		if (principal instanceof Admin) {
			//如果用户名是admin，就永远返回true
			if (StringUtils.equals(((Admin) principal).getUsername(), "admin")) {
				hasPermission = true;
			} else {
				// 读取用户所拥有权限的所有URL
				Set<String> urls = ((Admin) principal).getUrls();
				for (String url : urls) {
					if (antPathMatcher.match(url, request.getRequestURI())) {
						hasPermission = true;
						break;
					}
				}
			}
		}

		return hasPermission;
	}

}
