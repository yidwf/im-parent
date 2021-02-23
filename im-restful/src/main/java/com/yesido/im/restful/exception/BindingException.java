package com.yesido.im.restful.exception;

import org.springframework.validation.BindingResult;

/**
 * 数据验证异常
 * 
 * @author yesido
 * @date 2019年8月27日 上午10:27:04
 */
public class BindingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private BindingResult bindingResult;


    public BindingException(BindingResult br) {
        this.bindingResult = br;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

}
