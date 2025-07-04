package com.locker.common.exception.specific;

import com.locker.common.exception.base.CustomException;
import com.locker.common.exception.model.ErrorCode;

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

    /*public static AuthException tokenBlacklisted() {
        return new AuthException(ErrorCode.TOKEN_BLACKLISTED);
    }*/

}
