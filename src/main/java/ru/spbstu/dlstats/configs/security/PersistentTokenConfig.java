package ru.spbstu.dlstats.configs.security;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import ru.spbstu.dlstats.constants.SecretKeys;
import ru.spbstu.dlstats.services.detail.StaffDetailsServiceImpl;

@Configuration
public class PersistentTokenConfig {
    private final StaffDetailsServiceImpl staffDetailsService;
    @Value("${security.remember.time}")
    private Integer timeRemember;

    @Autowired
    public PersistentTokenConfig(StaffDetailsServiceImpl staffDetailsService) {
        this.staffDetailsService = staffDetailsService;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        val rememberMeServices = new TokenBasedRememberMeServices(SecretKeys.REMEMBER_ME_KEY, staffDetailsService);
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setTokenValiditySeconds(timeRemember);
        return rememberMeServices;
    }
}
