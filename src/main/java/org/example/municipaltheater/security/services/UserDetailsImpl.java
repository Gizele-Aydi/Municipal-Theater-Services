package org.example.municipaltheater.security.services;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.RegisteredUsers.Role;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final String userID;
    private final String username;
    private final String email;
    private final String password;
    private final Role role;

    public UserDetailsImpl(String userID, String username, String email, String password, Role role){
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static UserDetailsImpl build(RegisteredUser user) {
        return new UserDetailsImpl(user.getUserID(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRole());
    }

    public String getUserID() { return userID; }

    public String getEmail() { return email; }

    public Role getRole() { return role; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {return password;}

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(userID, user.userID);
    }
}
