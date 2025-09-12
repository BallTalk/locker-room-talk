package com.locker.board.domain;

import com.locker.exception.base.CustomException;
import com.locker.exception.model.ErrorCode;

public class BoardException extends CustomException {
    private BoardException(ErrorCode code) {
        super(code);
    }


}