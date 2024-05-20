package ru.spbstu.dlstats.provider;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.spbstu.dlstats.managers.StaffAuthenticationManager;

@Component
public class StaffAuthenticationProvider implements AuthenticationProvider {

    private final StaffAuthenticationManager authenticationManager;


    @Autowired
    public StaffAuthenticationProvider(StaffAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return this.authenticationManager.authenticate(authentication);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
