package com.giligency.zkweb.entity;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetJson<E> implements Serializable {
    public static final String success = "200";
    public static final String success_message = "请求成功";
    public static final String failure = "303";
    public static final String failure_message = "请求失败";
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

    private RetJson(String code, E data) {
        this.code = code;
        this.data = data;
    }

    public static <T> RetJson<T> retBean(String code, String message, T type) {
        return new RetJson<>(code, message, type);
    }

    public static <T> RetJson<T> retBean(String code, String message) {
        return new RetJson<>(code, message);
    }

    public static <T> RetJson<T> retBean(String code, T type) {
        return new RetJson<>(code, type);
    }

    public static <T> RetJson<T> retBean(String code) {
        return new RetJson<>(code);
    }

    public static <T> RetJson<T> retDefaultErrorBean(String message) {
        return new RetJson<>(RetJson.failure, message);
    }

    public static RetJson<Exception> retErrorBeanWithStack(String message, Exception e) {
        return new RetJson<>(RetJson.failure, message, e);
    }

    public static <T> RetJson<T> retDefaultSuccessBean(String message) {
        return new RetJson<>(RetJson.success, message);
    }

    /**
     * @param <T>
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static final class Success<T> extends RetJson<T> implements Serializable {
        private static final Success<?> success = new Success<>();
        private final String code = RetJson.success;
        private final String message = RetJson.success_message;
        private final T data = null;

        private Success() {

        }

        public static RetJson<?> getSuccess() {
            return success;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static final class Failure<T> extends RetJson<T> implements Serializable {
        private static final Failure<?> failure = new Failure<>();
        private final String code = RetJson.failure;
        private final String message = RetJson.failure_message;
        private final T data = null;

        private Failure() {

        }

        public static RetJson<?> getFailure() {
            return failure;
        }
    }
}
