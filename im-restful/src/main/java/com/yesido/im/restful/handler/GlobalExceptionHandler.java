package com.yesido.im.restful.handler;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.yesido.im.ptoto.api.HttpResult;
import com.yesido.im.restful.exception.AuthException;
import com.yesido.im.restful.exception.BindingException;
import com.yesido.im.restful.exception.BizException;
import com.yesido.im.restful.utils.ServletRequestUtil;
import com.yesido.im.restful.utils.ValidatorUtil;

/**
 * 全局异常处理
 * 
 * @author yesido
 * @date 2019年8月27日 上午10:23:30
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 异常错误处理
     * 
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HttpResult ExceptionHandler(HttpServletRequest request, Exception e) {
        if (e instanceof BindingException) {
            // 抛出自定义参数异常
            BindingException ex = (BindingException) e;
            BindingResult br = ex.getBindingResult();

            logValidator("[参数校验]BindingException：" + ValidatorUtil.getErrors(br, true), request, ex);
            return HttpResult.validatorError(ValidatorUtil.getErrors(br));
        } else if (e instanceof BindException) {
            // 接口方法参数@Validated或者@Valid + BindingResult
            BindException ex = (BindException) e;
            BindingResult br = ex.getBindingResult();

            logValidator("[参数校验]BindException：" + ValidatorUtil.getErrors(br, true), request, ex);
            return HttpResult.validatorError(ValidatorUtil.getErrors(br));
        } else if (e instanceof MethodArgumentNotValidException) {
            // @Validated或者@Valid校验规则： @Validated GroupModel model
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            BindingResult br = ex.getBindingResult();

            logValidator("[参数校验]MethodArgumentNotValidException：" + ValidatorUtil.getErrors(br, true), request, ex);
            return HttpResult.validatorError(ValidatorUtil.getErrors(br));
        } else if (e instanceof ConstraintViolationException) {
            // 参数校验规则：@NotBlank(message = "account不能为空") String account,
            ConstraintViolationException ex = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            logValidator("[参数校验]ConstraintViolationException：" + ValidatorUtil.getErrors(violations), request, ex);
            return HttpResult.validatorError(ValidatorUtil.getErrors(violations));
        } else if (e instanceof HttpMessageNotReadableException) {
            // json解析错误
            logValidator("[参数解析]HttpMessageNotReadableException", request, e);
            return HttpResult.serverError("无法解析参数");
        } else if (e instanceof AuthException) {
            logValidator("[token验证]AuthException：" + e.getMessage(), request, e);
            return HttpResult.unauthorized();
        } else if (e instanceof BizException) {
            BizException ex = (BizException) e;
            logValidator("[业务异常]BizException：" + ex.getMsg(), request, e);
            return HttpResult.fail(ex.getMsg());
        }

        log("未捕获异常", e);
        return HttpResult.serverError();
    }

    private void logValidator(String msg, HttpServletRequest request, Exception e) {
        String reqParam = ServletRequestUtil.getParameter(request);
        logger.error("{}，请求路径：{}，请求参数：{}", msg, request.getRequestURI(), reqParam);
    }

    private void log(String msg, Exception e) {
        log(msg, e, false);
    }

    private void log(String msg, Exception e, boolean simple) {
        if (simple) {
            logger.error("{}，异常堆栈：{}", msg, e.getLocalizedMessage());
        } else {
            logger.error(msg, e);
        }
    }
}
