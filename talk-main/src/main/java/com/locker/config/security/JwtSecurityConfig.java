package com.locker.config.security;

import com.locker.common.filter.PreventReLoginFilter;
import com.locker.config.jwt.JwtAuthenticationFilter;
import com.locker.config.jwt.JwtTokenProvider;
import com.locker.auth.application.JwtBlacklistService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtBlacklistService blacklistService;
    private final PreventReLoginFilter preventReLoginFilter;

    @Bean
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(tokenProvider, userDetailsService, blacklistService);

        http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(preventReLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(new SecurityContextLogoutHandler())
                        .addLogoutHandler((request, response, auth) -> {
                            Optional.ofNullable(request.getCookies()).stream()
                                    .flatMap(Arrays::stream)
                                    .filter(c -> "ACCESS_TOKEN".equals(c.getName()))
                                    .map(Cookie::getValue)
                                    .findFirst()
                                    .ifPresent(blacklistService::blacklist);
                        })
                        .logoutSuccessHandler((request, response, auth) -> {
                            Cookie deleteCookie = new Cookie("ACCESS_TOKEN", null);
                            deleteCookie.setPath("/");
                            deleteCookie.setHttpOnly(true);
                            deleteCookie.setSecure(false);  // HTTPS -> true
                            deleteCookie.setMaxAge(0);
                            response.addCookie(deleteCookie);
                            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                        })
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}