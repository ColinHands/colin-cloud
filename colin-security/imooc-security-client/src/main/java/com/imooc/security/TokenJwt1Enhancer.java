/**
 * 
 */
package com.imooc.security;

import com.imooc.entity.Admin;
import com.imooc.entity.RoleAdmin;
import com.imooc.entity.RoleResource;
import com.imooc.repository.AdminRepository;
import jdk.nashorn.internal.objects.annotations.Where;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhailiang
 *
 */
public class TokenJwt1Enhancer implements TokenEnhancer {

	@Autowired
	private AdminRepository adminRepository;

	/* (non-Javadoc)
	 * @see org.springframework.security.oauth2.provider.token.TokenEnhancer#enhance(org.springframework.security.oauth2.common.OAuth2AccessToken, org.springframework.security.oauth2.provider.OAuth2Authentication)
	 */
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> info = new HashMap<>();
		info.put("com", "com");
		Admin admin = (Admin)  authentication.getPrincipal();

		List<String> roleName = Lists.newArrayList();
		for (RoleAdmin roleAdmin : admin.getRoles()) {
			roleName.add(roleAdmin.getRole().getName());
		}
		admin.getUrls();
		info.put("role", roleName);
		info.put("urls", admin.getUrls());
		((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(info);
		
		return accessToken;
	}

}
