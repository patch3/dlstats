package ru.spbstu.dlstats.configs.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.spbstu.dlstats.constants.RoleConst;
import ru.spbstu.dlstats.constants.SecretKeys;
import ru.spbstu.dlstats.managers.StaffAuthenticationManager;


@Configuration
@EnableScheduling
@EnableWebSecurity
public class SecurityConfig {

    private final RememberMeServices rememberMeServices;

    private final StaffAuthenticationManager staffAuthenticationManager;

    @Autowired
    public SecurityConfig(RememberMeServices rememberMeServices,
                          StaffAuthenticationManager staffAuthenticationManager) {
        this.rememberMeServices = rememberMeServices;
        this.staffAuthenticationManager = staffAuthenticationManager;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) {
        return staffAuthenticationManager;
    }


    @Bean
    public SecurityFilterChain standardFilterChain(HttpSecurity http) throws Exception {
        //http.httpBasic(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests(
                        (request) -> request
                                .requestMatchers(
                                        "/staff/**"
                                ).hasRole(RoleConst.USER)
                                //).anonymous()
                                .requestMatchers(
                                        "/", "/home", "/index",
                                        "/login/process"
                                ).anonymous()
                                .requestMatchers(
                                        "/js/**",
                                        "/fonts/**",
                                        "/images/**",
                                        "/css/**",
                                        "/favicon.ico"
                                ).permitAll()
                                .anyRequest().authenticated()
                ).formLogin(
                        (form) -> form
                                .loginPage("/home")
                                .loginProcessingUrl("/login/process")
                                .failureUrl("/home?error")
                                .defaultSuccessUrl("/staff/menu")
                ).logout(
                        (logout) -> logout
                                .logoutUrl("/staff/logout")
                                .logoutSuccessUrl("/home?logout")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                ).csrf(
                        (csrf) -> csrf
                                //Customizer.withDefaults()
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                ).rememberMe(
                        (remember) -> remember
                                .rememberMeParameter("remember-me")
                                .key(SecretKeys.REMEMBER_ME_KEY)
                                .rememberMeServices(rememberMeServices)
                );
        return http.build();
    }
}
