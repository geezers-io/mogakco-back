package io.geezers.mogakco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@EnableWebSecurity
@SpringBootApplication
public class MogakcoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MogakcoApplication.class, args);
    }
}
