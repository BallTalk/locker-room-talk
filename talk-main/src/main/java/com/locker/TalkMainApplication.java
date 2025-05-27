package com.locker;

import com.locker.config.web.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CorsProperties.class)
public class TalkMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalkMainApplication.class, args);
    }

}
