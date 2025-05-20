package com.locker.common.exception.model;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // USER
    LOGIN_ID_REQUIRED(400, "로그인 아이디는 필수입니다."),
    PASSWORD_REQUIRED(400, "비밀번호는 필수입니다."),
    NICKNAME_REQUIRED(400, "닉네임은 필수입니다."),
    FAVORITE_TEAM_ID_REQUIRED(400, "응원 팀 ID는 필수입니다."),

    // COMMON
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    NOT_FOUND(404, "리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR(500, "서버 에러가 발생했습니다.");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
