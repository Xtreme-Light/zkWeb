package com.giligency.zkweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RetJson<E> implements Serializable {
    public static final String success = "200";
    public static final String success_message = "请求成功";
    public static final String failure = "303";
    public static final String failure_message = "请求失败";
    private static final RetJson<?> successBean = new RetJson<>();
    @NonNull
    private String code;
    private String message;
    private E data;

    private RetJson() {
        this.code = success;
        this.message = success_message;
    }

    public static RetJson<?> retSuccess() {
        return successBean;
    }

    public static <T> RetJson<T> retFailure(String code, String message, T type) {
        return new RetJson<>(code, message, type);
    }

    public static <T> RetJson<T> retFailure(String code, String message) {
        return new RetJson<>(code, message, null);
    }

    public static <T> RetJson<T> retFailure(String code, T type) {
        return new RetJson<>(code, null, type);
    }

    public static <T> RetJson<T> retFailure(String message) {
        return new RetJson<>(failure, message, null);
    }

}
