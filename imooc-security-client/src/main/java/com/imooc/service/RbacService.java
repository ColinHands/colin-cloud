/**
 * 
 */
package com.imooc.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhailiang
 *
 */
public interface RbacService {
	
	boolean hasPermission(HttpServletRequest request, Authentication authentication);

}
