package com.locker.domain;

import com.locker.exception.base.CustomException;
import com.locker.exception.model.ErrorCode;

public class UserException extends CustomException {
    private UserException(ErrorCode code) {
        super(code);
    }

    public static UserException loginIdBlank() {
        return new UserException(ErrorCode.LOGIN_ID_REQUIRED);
    }

    public static UserException loginIdDuplicate() {
        return new UserException(ErrorCode.LOGIN_ID_DUPLICATE);
    }

    public static UserException passwordNotMatch() {
        return new UserException(ErrorCode.PASSWORD_DO_NOT_MATCH);
    }

    public static UserException oldPasswordNotMatch() {
        return new UserException(ErrorCode.OLD_PASSWORD_NOT_MATCH);
    }

    public static UserException newPasswordNotMatch() {
        return new UserException(ErrorCode.NEW_PASSWORD_NOT_MATCH);
    }

    public static UserException userNotFound() {
        return new UserException(ErrorCode.USER_NOT_FOUND);
    }

    public static UserException userNotFoundByPhone() {
        return new UserException(ErrorCode.USER_NOT_FOUND_BY_PHONE);
    }

    public static UserException userStatusInvalid() {
        return new UserException(ErrorCode.USER_STATUS_INVALID);
    }

    public static UserException userNotFoundByProvider() {
        return new UserException(ErrorCode.USER_NOT_FOUND);
    }

}