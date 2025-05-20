package com.locker.common.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // USER
    LOGIN_ID_REQUIRED               (HttpStatus.BAD_REQUEST, "로그인 아이디는 필수입니다."),
    LOGIN_ID_LENGTH_INVALID         (HttpStatus.BAD_REQUEST, "로그인 아이디는 5~20자여야 합니다."),
    LOGIN_ID_PATTERN_INVALID        (HttpStatus.BAD_REQUEST, "로그인 아이디는 영문 대·소문자와 숫자 조합만 허용됩니다."),
    LOGIN_ID_DUPLICATE              (HttpStatus.CONFLICT, "중복된 로그인 아이디입니다."),
    PASSWORD_REQUIRED               (HttpStatus.BAD_REQUEST, "비밀번호는 필수입니다."),
    PASSWORD_LENGTH_INVALID         (HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상 72자 이하여야 합니다."),
    CONFIRM_PASSWORD_REQUIRED       (HttpStatus.BAD_REQUEST, "비밀번호 확인은 필수입니다."),
    CONFIRM_PASSWORD_LENGTH_INVALID (HttpStatus.BAD_REQUEST, "비밀번호 확인은 8자 이상 72자 이하여야 합니다."),
    PASSWORD_DO_NOT_MATCH           (HttpStatus.UNPROCESSABLE_ENTITY, "비밀번호가 일치하지 않습니다."),
    NICKNAME_REQUIRED               (HttpStatus.BAD_REQUEST, "닉네임은 필수입니다."),
    NICKNAME_LENGTH_INVALID         (HttpStatus.BAD_REQUEST, "닉네임은 5~20자여야 합니다."),
    FAVORITE_TEAM_ID_REQUIRED       (HttpStatus.BAD_REQUEST, "응원 팀 ID는 필수입니다."),

    // COMMON
    INVALID_REQUEST                 (HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND                       (HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR                  (HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");
    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
