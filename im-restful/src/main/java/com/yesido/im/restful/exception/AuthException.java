package com.yesido.im.restful.exception;

/**
 * 授权失败
 * 
 * @author yesido
 * @date 2019年8月28日 下午4:33:59
 */
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthException(String msg) {
        super(msg);
    }
}
