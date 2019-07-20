# SpringBoot2整合JWT解决Token跨域验证问题

## 一、JWT简介

  JWT(全称：JSON Web Token)，在基于HTTP通信过程中，进行身份认证。
  
### 1、认证过程

- 1、客户端通过用户名和密码登录服务器；
- 2、服务端对客户端身份进行验证；
- 3、服务器认证以后，生成一个 JSON 对象，发回客户端；
- 4、客户端与服务端通信的时候，都要发回这个 JSON 对象；
- 5、服务端解析该JSON对象，获取用户身份；
- 6、服务端可以不必存储该JSON（Token）对象，身份信息都可以解析出来。

### 2、JWT结构说明

  "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6iZEIj3fQ.uEJSJagJf1j7A55Wwr1bGsB5YQoAyz5rbFtF"
- 1、头部（header) 声明类型以及加密算法；
- 2、负载（payload) 携带一些用户身份信息；
- 3、签名（signature) 签名信息。


### 3、JWT使用方式
 通常推荐的做法是客户端在 HTTP 请求的头信息Authorization字段里面。
>Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6iZEIj3fQ.uEJSJagJf1j7A55Wwr1bGsB5YQoAyz5rbFtF

## 二、与SpringBoot2整合

### 1、核心依赖文件
```
<dependency>
 <groupId>io.jsonwebtoken</groupId>
 <artifactId>jjwt</artifactId>
 <version>0.7.0</version>
</dependency>
```
### 2、JWT配置代码块
```
package com.weishao.springtoken.config;

import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;

/**
 * Token 配置类
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

```

## 三、Token拦截案例

### 1、配置Token拦截器
```
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

```

### 2、拦截器注册
```
package com.weishao.springtoken.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 * @description 注册自己编写的Token拦截器TokenInterceptor
 *
 */
@Configuration
public class WebServiceConfig  implements WebMvcConfigurer{
	
	@Autowired
	private TokenInterceptor tokenInterceptor;
	
	public void addInterceptors(InterceptorRegistry registry) {
		 registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
	}
}

```

### 3、编写登录接口,生成token
```
package com.weishao.springtoken.controller;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.weishao.springtoken.config.JsonWebTokenConfig;
import com.weishao.springtoken.except.UnauthorizedException;
import io.swagger.annotations.ApiOperation;

/**
 * 登录认证相关接口类
 * @author tang
 *
 */
@RestController
public class LoginController  extends BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private JsonWebTokenConfig jwtConfig;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "登录认证", notes = "请使用合法的用户名和密码进行登录认证")
	public Map<String, Object> doLogin(@RequestBody Map<String, String> params) {
		String username = params.get("username");
		String password = params.get("password");

		if (username.equals("admin") && password.equals("admin123")) {
			String access_token = jwtConfig.createToken(username);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("access_token", access_token);
			data.put("token_type", "bearer");
			data.put("expires_in", JsonWebTokenConfig.EXPIRES_SECOND);
			
			logger.info("login success,username={},token={}", username, access_token);

			return success(data);
		} else {
			throw new UnauthorizedException(String.format("Invalid password for user [%s]", username));
		}

	}

}

```
### 4、编写认证测试接口
```
package com.weishao.springtoken.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户相关接口类
 *
 */
@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "获取用户信息 ", notes = "获取当前登录的用户基本信息")
	public Map<String, Object> doInfo(HttpServletRequest request) {
		String username = (String) request.getAttribute("identityId");
		logger.info("doInfo(),username={}",username);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("username", username);
		data.put("nickname", "manager");
		return success(data);
	}

}

```

## 四、调用测试

### 1、登录
```
 curl -X POST -d '{ "username": "admin", "password": "admin123"}' -H 'Content-Type: application/json' -s http://127.0.0.1:8090/login
```

### 2、验证
```
  curl -X GET -H 'Content-Type: application/json' -H 'Authorization: Bearer ${access_token}' http://127.0.0.1:8090/user/info
```
