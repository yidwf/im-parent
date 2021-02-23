package com.yesido.websocket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yesido.websocket.utils.JsonResult;

public class BaseController {

    @Autowired
    protected JsonResult jsonResult;

    public String validator(BindingResult br, boolean mode) {
        if (mode) {
            return validatorErrors(br);
        }
        return validatorErrorMessages(br);
    }

    public String validatorErrorMessages(BindingResult br) {
        if (br.hasFieldErrors()) {
            StringBuilder msg = new StringBuilder();
            List<FieldError> fieldErrors = br.getFieldErrors();
            for (int i = 0; i < fieldErrors.size(); i++) {
                if (i > 0) {
                    msg.append(",");
                }
                msg.append(fieldErrors.get(i).getDefaultMessage());
            }
            return msg.toString();
        }
        return null;
    }

    public String validatorErrors(BindingResult br) {
        if (br.hasFieldErrors()) {
            StringBuilder msg = new StringBuilder();
            List<FieldError> fieldErrors = br.getFieldErrors();
            for (int i = 0; i < fieldErrors.size(); i++) {
                if (i > 0) {
                    msg.append(",");
                }
                msg.append(fieldErrors.get(i).getField()).append(":").append(fieldErrors.get(i).getDefaultMessage());
            }
            return msg.toString();
        }
        return null;
    }
}
