package com.locker.auth.application;

import com.locker.exception.base.CustomException;
import com.locker.exception.model.ErrorCode;

public class AuthException extends CustomException {
    private AuthException(ErrorCode code) {
        super(code);
    }

    public static AuthException authenticationFailed() {
        return new AuthException(ErrorCode.AUTHENTICATION_FAILED);
    }

    public static AuthException unauthenticatedUser() {
        return new AuthException(ErrorCode.UNAUTHENTICATED_USER);
    }

    public static AuthException smsSendFailed() {
        return new AuthException(ErrorCode.SMS_SEND_FAILED);
    }

    public static AuthException smsCodeExpired() {
        return new AuthException(ErrorCode.SMS_CODE_EXPIRED);
    }

    public static AuthException smsCodeMismatch() {
        return new AuthException(ErrorCode.SMS_CODE_MISMATCH);
    }

    /*public static AuthException tokenBlacklisted() {
        return new AuthException(ErrorCode.TOKEN_BLACKLISTED);
    }*/

}
