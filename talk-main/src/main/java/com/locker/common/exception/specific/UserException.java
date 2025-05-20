package com.locker.common.exception.specific;

import com.locker.common.exception.base.CustomException;
import com.locker.common.exception.model.ErrorCode;

public class UserException extends CustomException {
    private UserException(ErrorCode code) {
        super(code);
    }

    public static UserException loginIdBlank() {
        return new UserException(ErrorCode.LOGIN_ID_REQUIRED);
    }
    public static UserException passwordBlank() {
        return new UserException(ErrorCode.PASSWORD_REQUIRED);
    }
}