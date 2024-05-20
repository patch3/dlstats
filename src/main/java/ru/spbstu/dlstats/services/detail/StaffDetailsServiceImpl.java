package ru.spbstu.dlstats.services.detail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.spbstu.dlstats.security.StaffDetails;
import ru.spbstu.dlstats.services.parse.ParseService;


@Lazy
@Service
@Scope("prototype")
public final class StaffDetailsServiceImpl implements UserDetailsService {
    private final ApplicationContext context;

    @Autowired
    public StaffDetailsServiceImpl(ApplicationContext context) {
        this.context = context;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new StaffDetails(username);
    }


    public boolean authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return false;

        var parseService = this.context.getBean(ParseService.class);
        var isAuthenticated = parseService.authenticate(username, password);
        parseService.close();
        return isAuthenticated;
    }
}
