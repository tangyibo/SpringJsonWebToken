package com.weishao.springtoken.config;

import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;

/**
 * Token 配置类
 * @author tang
 *
 */
@Configuration
public class JsonWebTokenConfig {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
	public static long EXPIRES_SECOND=5*60;
	
	private static String secret="iwqjhda8232bjgh432";
	

	/*
	 * 根据身份ID标识，生成Token
	 */
	public String createToken(String identityId) {
		Date nowDate = new Date();
		Date expireDate = new Date(nowDate.getTime() + EXPIRES_SECOND * 1000);
		return Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setSubject(identityId)
				.setIssuedAt(nowDate)
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	/*
	 * 获取 Token 中注册信息
	 */
	public Claims getTokenClaim(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	/*
	 * Token 是否过期验证
	 */
	public boolean isTokenExpired(Date expirationTime) {
		return expirationTime.before(new Date());
	}
}
