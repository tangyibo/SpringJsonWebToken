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
