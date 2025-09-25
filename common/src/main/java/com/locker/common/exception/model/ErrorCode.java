package com.locker.common.exception.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    AUTHENTICATION_FAILED           (HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHENTICATED_USER            (HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다."),
    TOKEN_BLACKLISTED               (HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다."),
    USER_NOT_FOUND                  (HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_NOT_FOUND_BY_PROVIDER      (HttpStatus.NOT_FOUND, "OAuth 사용자 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND_BY_PHONE         (HttpStatus.NOT_FOUND, "해당 휴대폰 번호로 등록된 사용자를 찾을 수 없습니다."),
    USER_STATUS_INVALID             (HttpStatus.FORBIDDEN, "로그인을 할 수 없는 계정상태입니다.(일시정지/영구밴/탈퇴)"),
    USER_ALREADY_AUTHENTICATED      (HttpStatus.FORBIDDEN,"이미 로그인된 사용자는 로그인 할 수 없습니다."),
    LOGIN_ID_REQUIRED               (HttpStatus.BAD_REQUEST, "로그인 아이디는 필수입니다."),
    LOGIN_ID_LENGTH_INVALID         (HttpStatus.BAD_REQUEST, "로그인 아이디는 5~20자여야 합니다."),
    LOGIN_ID_PATTERN_INVALID        (HttpStatus.BAD_REQUEST, "로그인 아이디는 영문 소문자와 숫자 조합만 허용됩니다."),
    LOGIN_ID_DUPLICATE              (HttpStatus.CONFLICT, "중복된 로그인 아이디입니다."),
    PASSWORD_REQUIRED               (HttpStatus.BAD_REQUEST, "비밀번호는 필수입니다."),
    PASSWORD_LENGTH_INVALID         (HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상 72자 이하여야 합니다."),
    CONFIRM_PASSWORD_REQUIRED       (HttpStatus.BAD_REQUEST, "비밀번호 확인은 필수입니다."),
    CONFIRM_PASSWORD_LENGTH_INVALID (HttpStatus.BAD_REQUEST, "비밀번호 확인은 8자 이상 72자 이하여야 합니다."),
    PASSWORD_DO_NOT_MATCH           (HttpStatus.UNPROCESSABLE_ENTITY, "비밀번호가 일치하지 않습니다."),
    OLD_PASSWORD_NOT_MATCH          (HttpStatus.UNPROCESSABLE_ENTITY, "기존 비밀번호가 올바르지 않습니다."),
    NEW_PASSWORD_NOT_MATCH          (HttpStatus.UNPROCESSABLE_ENTITY, "새 비밀번호가 일치하지 않습니다."),
    NICKNAME_REQUIRED               (HttpStatus.BAD_REQUEST, "닉네임은 필수입니다."),
    NICKNAME_LENGTH_INVALID         (HttpStatus.BAD_REQUEST, "닉네임은 5~20자여야 합니다."),
    PHONE_NUMBER_REQUIRED           (HttpStatus.BAD_REQUEST, "휴대폰 번호는 필수입니다."),
    PHONE_NUMBER_PATTERN_INVALID    (HttpStatus.BAD_REQUEST, "휴대폰 번호 형식이 올바르지 않습니다."),
    TEAM_CODE_REQUIRED              (HttpStatus.BAD_REQUEST, "응원 팀 선택은 필수입니다."),
    PROFILE_IMAGE_URL_TOO_LONG      (HttpStatus.BAD_REQUEST, "프로필 이미지 URL 길이는 255자 이하여야 합니다."),
    STATUS_MESSAGE_TOO_LONG         (HttpStatus.BAD_REQUEST, "상태 메시지는 200자 이하여야 합니다."),
    SMS_PURPOSE_REQUIRED            (HttpStatus.BAD_REQUEST, "SMS 인증 용도는 필수입니다."),
    SMS_SEND_FAILED                 (HttpStatus.INTERNAL_SERVER_ERROR, "SMS 전송에 실패했습니다."),
    SMS_CODE_EXPIRED                (HttpStatus.BAD_REQUEST, "인증번호가 없거나 만료되었습니다."),
    SMS_CODE_MISMATCH               (HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    TEAM_NOT_FOUND                  (HttpStatus.NOT_FOUND, "존재하지 않는 팀 코드입니다."),
    MENU_NOT_FOUND                  (HttpStatus.NOT_FOUND, "조회되는 메뉴가 없습니다."),
    BOARD_ID_REQUIRED               (HttpStatus.BAD_REQUEST, "게시판 아이디는 필수입니다."),
    BOARD_ID_MUST_BE_POSITIVE       (HttpStatus.BAD_REQUEST, "게시판 아이디는 양수여야 합니다."),
    POST_ID_MUST_BE_POSITIVE        (HttpStatus.BAD_REQUEST, "게시글 아이디는 양수여야 합니다."),

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
