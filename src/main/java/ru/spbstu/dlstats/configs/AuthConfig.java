package ru.spbstu.dlstats.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AuthConfig {
    @Value("${parse.auth.url}")
    private String authUrl;

    @Value("${parse.auth.profile.username}")
    private String username;

    @Value("${parse.auth.profile.password}")
    private String password;
}
