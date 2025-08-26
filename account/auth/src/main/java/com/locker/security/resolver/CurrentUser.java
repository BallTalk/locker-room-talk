package com.locker.security.resolver;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
    // @CurrentUser -> ArgumentResolver로 현재 인증된 사용자의 loginId 주입
}
