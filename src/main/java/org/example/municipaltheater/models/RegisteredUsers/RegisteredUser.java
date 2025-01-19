package org.example.municipaltheater.models.RegisteredUsers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.example.municipaltheater.models.ShowModels.Ticket;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;


import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@ToString
@Document(collection = "Registered Users")

public class RegisteredUser {

    @Id
    @Generated
    private String userID;
    @JsonProperty("username")
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

}