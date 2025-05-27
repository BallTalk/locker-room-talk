package com.locker.config.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsProperties {
    private String allowedOrigins;
}
