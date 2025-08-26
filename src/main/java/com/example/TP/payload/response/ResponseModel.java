package com.example.TP.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;

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
        this.data = null;
    }

    public ResponseModel() {
    }

    // Instance methods (keep these for backward compatibility)
    public ResponseModel<T> success(int status, T data) {
        return new ResponseModel<>(status, true, null, data);
    }

    public ResponseModel<T> success(int status, String message, T data) {
        return new ResponseModel<>(status, true, message, data);
    }

    public ResponseModel<T> failed(int status, String message, T data) {
        return new ResponseModel<>(status, false, message, data);
    }

    public ResponseModel<T> failed(int status, String message) {
        return new ResponseModel<>(status, false, message, null);
    }

    // Static factory methods for PageResponse failure
    public static <R> ResponseModel<PageResponse<R>> failedPage(int status, String message) {
        return new ResponseModel<>(status, false, message, null);
    }

    public static <R> ResponseModel<PageResponse<R>> failedPage(int status, String message, PageResponse<R> data) {
        return new ResponseModel<>(status, false, message, data);
    }

    // Static factory methods for generic failure (for non-page responses)
    public static <T> ResponseModel<T> failed(int status, String message, Class<T> type) {
        return new ResponseModel<>(status, false, message, null);
    }

    // Page response success methods
    public static <R> ResponseModel<PageResponse<R>> successPage(int status, Page<R> page) {
        PageResponse<R> pageResponse = new PageResponse<>(
                page.getContent(),
                page.getNumber(), // This returns 0-based page index
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
        return new ResponseModel<>(status, true, null, pageResponse);
    }

    public static <R> ResponseModel<PageResponse<R>> successPage(int status, String message, Page<R> page) {
        PageResponse<R> pageResponse = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
        return new ResponseModel<>(status, true, message, pageResponse);
    }

    // InputStreamResource method (keep if needed)
    public ResponseModel<InputStreamResource> successWithResource(T resource) {
        if (resource instanceof InputStreamResource) {
            return new ResponseModel<>(status, true, message, (InputStreamResource) resource);
        }
        throw new IllegalArgumentException("Resource must be InputStreamResource");
    }
}