package com.locker.menu.domain;

import com.locker.common.exception.base.CustomException;
import com.locker.common.exception.model.ErrorCode;

public class MenuException extends CustomException {
    private MenuException(ErrorCode code) {
        super(code);
    }

    public static MenuException menuNotFound() {
        return new MenuException(ErrorCode.MENU_NOT_FOUND);
    }
}