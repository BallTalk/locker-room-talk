package com.locker.config.security;


import com.locker.auth.application.CustomOAuth2UserService;
import com.locker.common.filter.PreventReLoginFilter;
import com.locker.config.jwt.OAuth2JwtSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
public class OAuth2SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2JwtSuccessHandler oauthJwtSuccessHandler;
    private final PreventReLoginFilter preventReLoginFilter;

    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(preventReLoginFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .securityMatcher("/oauth2/**", "/login/oauth2/code/**")

                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oauthJwtSuccessHandler)
                );

        return http.build();
    }
}