package com.yesido.im.restful.exception;

/**
 * 业务异常
 * 
 * @author yesido
 * @date 2019年8月29日 下午2:38:07
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String msg;

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
