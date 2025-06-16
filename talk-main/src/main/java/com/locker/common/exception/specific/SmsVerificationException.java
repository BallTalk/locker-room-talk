package com.locker.common.exception.specific;

import com.locker.common.exception.base.CustomException;
import com.locker.common.exception.model.ErrorCode;

public class SmsVerificationException extends CustomException {
    private SmsVerificationException(ErrorCode code) {
        super(code);
    }

    public static SmsVerificationException sendFailed() {
        return new SmsVerificationException(ErrorCode.SMS_SEND_FAILED);
    }

    public static SmsVerificationException codeExpired() {
        return new SmsVerificationException(ErrorCode.SMS_CODE_EXPIRED);
    }

    public static SmsVerificationException codeMismatch() {
        return new SmsVerificationException(ErrorCode.SMS_CODE_MISMATCH);
    }

}
