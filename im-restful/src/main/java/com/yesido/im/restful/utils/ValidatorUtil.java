package com.yesido.im.restful.utils;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yesido.im.restful.exception.BindingException;

/**
 * 数据验证工具类
 * 
 * @author yesido
 * @date 2019年8月27日 上午10:11:08
 */
public class ValidatorUtil {

    /**
     * 是否有错误信息
     */
    public static boolean hasErrors(BindingResult br) {
        if (br == null) {
            return false;
        }
        return br.hasFieldErrors();
    }

    /**
     * 抛出错误异常
     */
    public static void throwErrorIfError(BindingResult br) {
        if (hasErrors(br)) {
            throw new BindingException(br);
        }
    }

    /**
     * 获取错误信息
     */
    public static String getErrors(Set<ConstraintViolation<?>> violations) {
        if (violations == null || violations.isEmpty()) {
            return null;
        }
        StringBuilder msg = new StringBuilder();
        int size = 0;
        for (ConstraintViolation<?> violation : violations) {
            if (size >= 1) {
                msg.append(",");
            }
            msg.append(violation.getMessage());
            size++;
        }
        return msg.toString();
    }

    /**
     * 返回错误信息，不含字段名称
     */
    public static String getErrors(BindingResult br) {
        return getErrors(br, false);
    }

    /**
     * 返回错误信息
     * 
     * @param br 数据绑定验证结果
     * @param appendField 错误信息是否包含字段名称
     * @return
     */
    public static String getErrors(BindingResult br, boolean appendField) {
        if (!hasErrors(br)) {
            return null;
        }
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = br.getFieldErrors();
        for (int i = 0; i < fieldErrors.size(); i++) {
            if (i > 0) {
                msg.append(",");
            }
            if (appendField) {
                msg.append(fieldErrors.get(i).getField()).append(":");
            }
            msg.append(fieldErrors.get(i).getDefaultMessage());
        }
        return msg.toString();
    }
}
