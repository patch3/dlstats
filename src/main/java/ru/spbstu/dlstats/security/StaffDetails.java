package ru.spbstu.dlstats.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.spbstu.dlstats.constants.RoleConst;

import java.util.Collection;
import java.util.Collections;

@Data
public class StaffDetails implements UserDetails {
    private String username;
    private String password;

    public StaffDetails(String username) {
        this.username = username;
        this.password = null;
    }

    public StaffDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + RoleConst.USER));
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
