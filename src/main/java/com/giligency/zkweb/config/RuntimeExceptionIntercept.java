package com.giligency.zkweb.config;

import com.giligency.zkweb.entity.RetJson;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RuntimeExceptionIntercept {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public RetJson<?> ifRuntimeExceptionHappened(RuntimeException e) {
        return RetJson.retBean("303", e.getMessage(), e);
    }
}
