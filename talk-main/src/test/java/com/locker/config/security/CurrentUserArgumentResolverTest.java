package com.locker.config.security;

import com.locker.common.exception.specific.AuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentUserArgumentResolverTest {

    private CurrentUserArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new CurrentUserArgumentResolver();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @SuppressWarnings("unused")
    private void 핸들러_메서드에_CurrentUser_가_붙은_String_파라미터(@CurrentUser String loginId) {
    }

    @SuppressWarnings("unused")
    private void 핸들러_메서드에_CurrentUser_가_없는_String_파라미터(String someParam) {
    }

    @SuppressWarnings("unused")
    private void 핸들러_메서드에_CurrentUser_가_붙었지만_String_타입이_아닌_파라미터(@CurrentUser Integer notStringId) {
    }

    @Test
    void CurrentUser_어노테이션이_없는_경우_false_를_반환한다() throws NoSuchMethodException {
        // Given
        Method method = CurrentUserArgumentResolverTest.class
                .getDeclaredMethod("핸들러_메서드에_CurrentUser_가_없는_String_파라미터", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // When
        boolean supports = resolver.supportsParameter(parameter);

        // Then
        assertFalse(supports, "@CurrentUser가 없는 파라미터는 false 리턴");
    }

    @Test
    void CurrentUser_어노테이션이_있지만_String_타입이_아니면_false_를_반환한다() throws NoSuchMethodException {
        // Given
        Method method = CurrentUserArgumentResolverTest.class
                .getDeclaredMethod("핸들러_메서드에_CurrentUser_가_붙었지만_String_타입이_아닌_파라미터", Integer.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // When
        boolean supports = resolver.supportsParameter(parameter);

        // Then
        assertFalse(supports, "@CurrentUser는 붙었지만 타입이 String이 아니면 false 리턴");
    }

    @Test
    void CurrentUser_어노테이션이_있고_인증된_상태면_loginId가_반환된다() throws Exception {
        // Given
        String expectedLoginId = "테스트사용자";
        Authentication auth = new UsernamePasswordAuthenticationToken(
                expectedLoginId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Method method = CurrentUserArgumentResolverTest.class
                .getDeclaredMethod("핸들러_메서드에_CurrentUser_가_붙은_String_파라미터", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);

        // When
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        // Then
        assertNotNull(result, "인증된 사용자의 경우 null 이 아니어야 한다");
        assertEquals(expectedLoginId, result, "resolveArgument는 Authentication.getName() 값을 반환해야 한다");
    }

    @Test
    void CurrentUser_어노테이션이_있는데_비인증_상태면_AuthException을_던진다() throws Exception {
        // Given: 인증 정보가 없는 상태로 설정
        SecurityContextHolder.clearContext();

        Method method = CurrentUserArgumentResolverTest.class
                .getDeclaredMethod("핸들러_메서드에_CurrentUser_가_붙은_String_파라미터", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);

        // When & Then
        assertThrows(
                AuthException.class,
                () -> resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory),
                "비인증 상태에서는 AuthException이 던져져야 한다"
        );
    }
}