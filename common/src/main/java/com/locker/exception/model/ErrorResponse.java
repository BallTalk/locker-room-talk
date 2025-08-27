package com.locker.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ErrorResponse {

    private HttpStatus status;
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
