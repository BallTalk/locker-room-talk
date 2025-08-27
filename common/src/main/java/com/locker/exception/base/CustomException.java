package com.locker.exception.base;

import com.locker.exception.model.ErrorCode;
import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    protected CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
