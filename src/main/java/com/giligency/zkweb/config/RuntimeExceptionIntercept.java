package com.giligency.zkweb.config;

import com.alibaba.fastjson.JSON;
import com.giligency.zkweb.entity.RetJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(annotations = {RestController.class, Controller.class})//配置需要拦截的类
@Slf4j
public class RuntimeExceptionIntercept {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<RetJson<RuntimeException>> ifRuntimeExceptionHappened(RuntimeException ex,
                                                                                HttpServletRequest request) {
        // 注入servletRequest，用于出错时打印请求URL与来源地址
        logError(ex, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        RetJson<RuntimeException> runtimeExceptionRetJson = RetJson.retFailure("303", ex.getMessage(), ex);//默认303为错误.
        return new ResponseEntity<>(runtimeExceptionRetJson, headers, HttpStatus.OK);
    }

    public void logError(Exception ex, HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("from", request.getRemoteAddr());
        String queryString = request.getQueryString();
        map.put("path", queryString != null ? (request.getRequestURI() + "?" + queryString) : request.getRequestURI());

        log.error(JSON.toJSONString(map), ex);
    }
}
