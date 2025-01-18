package org.example.municipaltheater.models.RegisteredUsers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import org.example.municipaltheater.models.ShowModels.Ticket;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;


import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@ToString
@Document(collection = "Registered Users")

public class RegisteredUser {

    @Id
    @Generated
    private String userID;
    @Indexed(unique = true) @Size(max = 20) @NotBlank(message = "Username is a required field.")
    private String username;
    @Indexed(unique = true) @Size(max = 50) @NotBlank(message = "Email is a required field.")
    private String email;
    @Size(max = 120) @NotBlank(message = "Password is a required field.")
    private String password;
    private Role role;
    @DBRef(lazy = true) @JsonInclude(JsonInclude.Include.NON_NULL) @Field("bookedTickets")
    private List<Ticket> bookedTickets = new ArrayList<>();
    @DBRef(lazy = true)
    private List<Ticket> History = new ArrayList<>();
    //private String VerifCode;
    //private LocalDateTime VerifCodeExpiresAt;
    //private boolean enabled;

    public RegisteredUser(){
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Ticket> getBookedTickets() {
        return bookedTickets;
    }

    public void setBookedTickets(List<Ticket> bookedTickets) {
        this.bookedTickets = bookedTickets;
    }

    public List<Ticket> getHistory() {
        return History;
    }

    public void setHistory(List<Ticket> history) {
        History = history;
    }

    /*public String getVerifCode() {
        return VerifCode;
    }

    public void setVerifCode(String verifCode) {
        VerifCode = verifCode;
    }

    public LocalDateTime getVerifCodeExpiresAt() {
        return VerifCodeExpiresAt;
    }

    public void setVerifCodeExpiresAt(LocalDateTime verifCodeExpiresAt) {
        VerifCodeExpiresAt = verifCodeExpiresAt;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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
        return enabled;
    }*/
}