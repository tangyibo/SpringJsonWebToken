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
 * @author tang
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
