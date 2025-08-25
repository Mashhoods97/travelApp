package com.example.TP.security.services;

import com.example.TP.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {

    @Getter
    private final Long id;
    //    private final String title;
    private final String username;
    private final String name;
    private final String secondaryName;
    @Getter
    private final Long businessId;
    @Getter
    private final String email;
    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String name, String secondaryName, String username, String email, String password, Long businessId, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.secondaryName = secondaryName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.businessId = businessId;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRole().getPrivileges().stream()
                .map(eRole -> new SimpleGrantedAuthority(eRole.getErole().name())).collect(Collectors.toList());
        return new UserDetailsImpl(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getBusinessId(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
