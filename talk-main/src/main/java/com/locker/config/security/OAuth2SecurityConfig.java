package com.locker.config.security;


import com.locker.auth.temp.CustomOAuth2UserService;
import com.locker.config.jwt.OAuth2JwtSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
@RequiredArgsConstructor
public class OAuth2SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2JwtSuccessHandler oauthJwtSuccessHandler;

    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/oauth2/code/**")

                .csrf(csrf -> csrf.disable())
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