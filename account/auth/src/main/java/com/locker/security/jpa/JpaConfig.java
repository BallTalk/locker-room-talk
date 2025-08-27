package com.locker.security.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.locker")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null
                    || !auth.isAuthenticated()
                    || auth instanceof AnonymousAuthenticationToken) {
                // 회원가입(비인증) 시
                return Optional.of("anonymous");
            }
            // 인증된 사용자의 로그인 아이디
            return Optional.of(((UserDetails) auth.getPrincipal()).getUsername());
        };
    }

}