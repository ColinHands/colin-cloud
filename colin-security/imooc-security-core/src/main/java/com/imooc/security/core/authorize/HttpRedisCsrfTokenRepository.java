/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imooc.security.core.authorize;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A {@link CsrfTokenRepository} that stores the {@link CsrfToken} in the
 * {@link HttpSession}.
 *
 * @author Rob Winch
 * @since 3.2
 */
@Component
public final class HttpRedisCsrfTokenRepository implements CsrfTokenRepository {
	private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

	private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

	private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpRedisCsrfTokenRepository.class
			.getName().concat(".CSRF_TOKEN");

	private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

	private String headerName = DEFAULT_CSRF_HEADER_NAME;

	private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;

	private RedisTemplate<Object, Object> redisTemplate;

	@Autowired
	public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.security.web.csrf.CsrfTokenRepository#saveToken(org.
	 * springframework .security.web.csrf.CsrfToken,
	 * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void saveToken(CsrfToken token, HttpServletRequest request,
			HttpServletResponse response) {
		if (token == null) {
			return;
		}
		redisTemplate.opsForValue().set(token.getToken(), null, 120, TimeUnit.SECONDS);
		response.setHeader(this.headerName, token.getToken());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.security.web.csrf.CsrfTokenRepository#loadToken(javax.servlet
	 * .http.HttpServletRequest)
	 */
	public CsrfToken loadToken(HttpServletRequest request) {
		final String csrfToken = getCachedToken(request);
		return StringUtils.isBlank(csrfToken) ? null : new DefaultCsrfToken(
				this.headerName,
				this.parameterName,
				csrfToken
		);
	}

	private String getCachedToken(HttpServletRequest request) {
		try {
			String tokenKey = request.getHeader(this.headerName);
			boolean isExpire = redisTemplate.hasKey(tokenKey);
			if (isExpire) {
				return tokenKey;
			}
		} catch (Exception e) {
			return StringUtils.EMPTY;
		}
		return StringUtils.EMPTY;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.security.web.csrf.CsrfTokenRepository#generateToken(javax.
	 * servlet .http.HttpServletRequest)
	 */
	public CsrfToken generateToken(HttpServletRequest request) {
		return new DefaultCsrfToken(this.headerName, this.parameterName,
				createNewToken());
	}

	/**
	 * Sets the {@link HttpServletRequest} parameter name that the {@link CsrfToken} is
	 * expected to appear on
	 * @param parameterName the new parameter name to use
	 */
	public void setParameterName(String parameterName) {
		Assert.hasLength(parameterName, "parameterName cannot be null or empty");
		this.parameterName = parameterName;
	}

	/**
	 * Sets the header name that the {@link CsrfToken} is expected to appear on and the
	 * header that the response will contain the {@link CsrfToken}.
	 *
	 * @param headerName the new header name to use
	 */
	public void setHeaderName(String headerName) {
		Assert.hasLength(headerName, "headerName cannot be null or empty");
		this.headerName = headerName;
	}

	/**
	 * Sets the {@link HttpSession} attribute name that the {@link CsrfToken} is stored in
	 * @param sessionAttributeName the new attribute name to use
	 */
	public void setSessionAttributeName(String sessionAttributeName) {
		Assert.hasLength(sessionAttributeName,
				"sessionAttributename cannot be null or empty");
		this.sessionAttributeName = sessionAttributeName;
	}

	private String createNewToken() {
		return UUID.randomUUID().toString();
	}

	public static HttpRedisCsrfTokenRepository withHttpOnlyFalse() {
		return new HttpRedisCsrfTokenRepository();
	}
}
