package com.example.TP.utils;

import com.example.TP.payload.response.ResponseModel;
import org.springframework.http.HttpStatus;

public class GeneralException extends RuntimeException{
    private final HttpStatus httpStatus;

    public static final HttpStatus HTTP_OK = HttpStatus.OK;
    public static final HttpStatus HTTP_NOT_FOUND = HttpStatus.NOT_FOUND;
    public static final HttpStatus HTTP_FORBIDDEN = HttpStatus.FORBIDDEN;
    public static final HttpStatus HTTP_BAD_REQUEST = HttpStatus.BAD_REQUEST;
    public static final HttpStatus HTTP_INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;

    public GeneralException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public static GeneralException badRequest(String message) {
        return new GeneralException(message, HTTP_BAD_REQUEST);
    }

    public static GeneralException notFound(String message) {
        return new GeneralException(message, HTTP_NOT_FOUND);
    }

    public static GeneralException forbidden(String message) {
        return new GeneralException(message, HTTP_FORBIDDEN);
    }


    public static GeneralException internalServerError(String message) {
        return new GeneralException(message, HTTP_INTERNAL_SERVER_ERROR);
    }

    public ResponseModel<?> toResponseModel() {
        return new ResponseModel<>(
                httpStatus.value(),
                false,
                getMessage(),
                null
        );
    }
}
