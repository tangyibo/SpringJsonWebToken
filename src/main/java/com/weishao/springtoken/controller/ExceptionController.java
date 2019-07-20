package com.weishao.springtoken.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.weishao.springtoken.except.UnauthorizedException;

/**
 * 异常返回处理
 * @author tang
 *
 */
@RestControllerAdvice
public class ExceptionController  extends BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

	//401返回
    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> unauthorized() {
    	logger.info("401 page");
        return failed(401, "Unauthorized");
    }
    
    // 捕捉UnauthorizedException
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Map<String, Object>  handle401(UnauthorizedException e) {
    	logger.info("UNAUTHORIZED error:",e);
    	return failed(401, "Unauthorized");
    }

    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> globalException(HttpServletRequest request, Throwable ex) {
    	logger.info("error:",ex);
    	return failed(401,ex.getMessage());
    }
}
