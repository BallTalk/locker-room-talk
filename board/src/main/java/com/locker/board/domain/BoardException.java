package com.locker.board.domain;

import com.locker.common.exception.base.CustomException;
import com.locker.common.exception.model.ErrorCode;

public class BoardException extends CustomException {
    private BoardException(ErrorCode code) {
        super(code);
    }


}