package com.locker.common.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Data
public class ErrorResponse {

    private int status;
    private String message;
    private List<FieldError> errors;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class FieldError {
        private String field;
        private String errorMessage;
    }

}
