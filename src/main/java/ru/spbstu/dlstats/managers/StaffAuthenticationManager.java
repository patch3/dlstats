package ru.spbstu.dlstats.managers;

import lombok.val;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.spbstu.dlstats.constants.RoleConst;
import ru.spbstu.dlstats.services.parse.ParseService;

import java.util.Collections;

@Component
public class StaffAuthenticationManager implements AuthenticationManager {
    private final ApplicationContext context;

    public StaffAuthenticationManager(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        val username = authentication.getName();
        val password = authentication.getCredentials().toString();

        if (username.isEmpty() || password.isEmpty())
            throw new BadCredentialsException("Пустые поля");

        val parseService = this.context.getBean(ParseService.class);
        val isAuthenticated = parseService.authenticate(username, password);
        parseService.close();

        if (!isAuthenticated) {
            throw new BadCredentialsException("Неверное имя пользователя или пароль");
        }

        return new UsernamePasswordAuthenticationToken(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + RoleConst.USER))
        );
    }
}
