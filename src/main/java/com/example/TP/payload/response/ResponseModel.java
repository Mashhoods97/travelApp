package com.example.TP.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.InputStreamResource;

@Getter
@Setter
public class ResponseModel<T> {
    private int status;
    private String message;
    private boolean success;
    private T data;

    public ResponseModel(int status, boolean success, String message, T data) {
        this.status = status;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public ResponseModel(int status, boolean success, String message) {
        this.status = status;
        this.message = message;
        this.success = success;
    }

    public ResponseModel() {
    }

    public ResponseModel success(int status, T data) {
        return new ResponseModel(status, true, null, data);
    }

    public ResponseModel success(int status, String message, T data) {
        return new ResponseModel(status, true, message, data);
    }

    public ResponseModel failed(int status, String message, T data) {
        return new ResponseModel(status, false, message, data);
    }
    public ResponseModel failed(int status, String message) {
        return new ResponseModel(status, false, message);
    }

    public ResponseModel<InputStreamResource> success(T resource) {return new ResponseModel(status, true, message, resource);
    }
}

