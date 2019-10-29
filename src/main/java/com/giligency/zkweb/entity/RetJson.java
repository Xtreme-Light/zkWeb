package com.giligency.zkweb.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetJson<E> implements Serializable {
    private String code;
    private String message;
    private E data;

    public static RetJson returnSuccess(String message) {
        final RetJson<Object> objectRetJson = new RetJson<>();
        objectRetJson.setCode(Status.SUCCESS.toString());
        objectRetJson.setMessage(message);
        return objectRetJson;
    }

    public static RetJson returnFailure(String message) {
        final RetJson<Object> objectRetJson = new RetJson<>();
        objectRetJson.setCode("303");
        objectRetJson.setMessage(message);
        return objectRetJson;
    }

    public enum Status {
        SUCCESS("200"), FAILURE("303");
        private String code;

        Status(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

}
