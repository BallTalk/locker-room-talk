package com.locker.exception.handler;

import com.locker.exception.base.CustomException;
import com.locker.exception.model.ErrorCode;
import com.locker.exception.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustom(CustomException ex) {
        ErrorCode code = ex.getErrorCode();
        ErrorResponse body = new ErrorResponse(code);

        log.warn("비즈니스 예외 발생 – code: {} message: {}", code, ex.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> {
                    String defaultMsg = fe.getDefaultMessage();
                    String finalMsg;

                    try {
                        ErrorCode ec = ErrorCode.valueOf(defaultMsg);
                        finalMsg = ec.getMessage();
                    } catch (IllegalArgumentException ie) {
                        finalMsg = defaultMsg;
                    }

                    return new ErrorResponse.FieldError(
                            fe.getField(),
                            finalMsg
                    );
                })
                .collect(Collectors.toList());

        log.info("입력 검증 실패 – field: {}", errors);

        ErrorResponse body = new ErrorResponse(ErrorCode.INVALID_REQUEST);
        body.setErrors(errors);
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {

        log.error("미처리 예외 발생", ex);

        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        ErrorResponse body = new ErrorResponse(code);
        return ResponseEntity
                .status(code.getStatus())
                .body(body);
    }

}
