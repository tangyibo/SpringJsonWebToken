package com.weishao.springtoken.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import io.jsonwebtoken.Claims;

/**
 * Token拦截器
 * 
 * @description 拦截Http头中的Authorization值，截取出toke并进行有消息验证
 * @author tang
 */
@Component
public class TokenInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

	@Autowired
	private JsonWebTokenConfig jwtConfig;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String uri = request.getRequestURI();
		if (uri.contains("/login")) {
			return true;
		}

		String authorization = request.getHeader(JsonWebTokenConfig.TOKEN_HEADER);
		if (authorization == null || authorization.isEmpty()
				|| !authorization.startsWith(JsonWebTokenConfig.TOKEN_PREFIX)) {
			throw new Exception("Authorization is empty!");
		}

		String token = authorization.replace(JsonWebTokenConfig.TOKEN_PREFIX, "");
		Claims claims = jwtConfig.getTokenClaim(token);
		if (claims == null) {
			throw new Exception("parse token error");
		} else if (jwtConfig.isTokenExpired(claims.getExpiration())) {
			throw new Exception("token is expired , please login");
		}

		// 设置 identityId 用户身份ID
		String identityId = claims.getSubject();
		request.setAttribute("identityId", identityId);
		logger.info("[Request] {} ,token={},identityId={}", uri, token, identityId);

		return true;
	}
}
