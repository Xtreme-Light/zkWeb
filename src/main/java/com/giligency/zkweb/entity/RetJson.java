package com.giligency.zkweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetJson<E> implements Serializable {
    public static String success = "200";
    public static String success_message = "请求成功";
    private static final RetJson retSuccess = new RetJson(success);
    public static String failure = "303";
    private static final RetJson retFailure = new RetJson(failure);
    @NonNull
    private String code;
    private String message;
    private E data;

    private RetJson(String code) {
        this.code = code;
    }

    private RetJson(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> RetJson<T> newBean(T type) {
        return new RetJson<>();

    }

    @SuppressWarnings("unchecked")
    public static RetJson returnSuccess() {
        retSuccess.setMessage(null);
        retSuccess.setData(null);
        return retSuccess;
    }
    public static RetJson returnSuccess(String message) {
        final RetJson objectRetJson = new RetJson();
        objectRetJson.setCode(success);
        objectRetJson.setMessage(message);
        return objectRetJson;
    }

    public static <T> RetJson<T> returnSuccess(String message, T data) {
        final RetJson<T> objectRetJson = new RetJson<>();
        objectRetJson.setCode(success);
        objectRetJson.setMessage(message);
        objectRetJson.setData(data);
        return objectRetJson;
    }

    @SuppressWarnings("unchecked")
    public static RetJson returnFailure() {
        retFailure.setMessage(null);
        retFailure.setData(null);
        return retFailure;
    }
    public static RetJson returnFailure(String message) {
        final RetJson objectRetJson = new RetJson();
        objectRetJson.setCode(failure);
        objectRetJson.setMessage(message);
        return objectRetJson;
    }

    public static <T> RetJson<T> returnFailure(String message, T data) {
        final RetJson<T> objectRetJson = new RetJson<>();
        objectRetJson.setCode(failure);
        objectRetJson.setMessage(message);
        return objectRetJson;
    }

    public static <T> RetJson<T> buildJson(String code, String message, T data) {
        final RetJson<T> objectRetJson = new RetJson<>();
        objectRetJson.setCode(code);
        objectRetJson.setCode(failure);
        objectRetJson.setMessage(message);
        return objectRetJson;
    }

}
