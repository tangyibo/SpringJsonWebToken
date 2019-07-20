package com.weishao.springtoken.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 * @description 注册自己编写的Token拦截器TokenInterceptor
 * @author tang
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
