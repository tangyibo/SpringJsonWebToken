package com.weishao.springtoken.except;

/**
 * 未授权异常类
 * @author tang
 *
 */
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = -1750119009948024152L;

	public UnauthorizedException(String msg) {
        super(msg);
    }

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}

	public UnauthorizedException(String message, Throwable cause, boolean enableSuppression,boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}