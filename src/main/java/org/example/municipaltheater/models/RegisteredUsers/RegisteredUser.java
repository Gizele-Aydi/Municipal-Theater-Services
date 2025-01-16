package org.example.municipaltheater.models.RegisteredUsers;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Indexed(unique = true)
    @NotBlank(message = "Username is a required field.")
    private String username;
    @Indexed(unique = true)
    @NotBlank(message = "Email is a required field.")
    private String email;
    @NotBlank(message = "Password is a required field.")
    private String Password;
    @Indexed(unique = true)
    private String PhoneNum;
    private Role role;
    @DBRef(lazy = true)
    @Field("bookedTickets")
    private List<Ticket> bookedTickets = new ArrayList<>();
    @DBRef(lazy = true)
    private List<Ticket> History = new ArrayList<>();

    public RegisteredUser() {
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
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Ticket> getBookedTickets() { return bookedTickets;}

    public void setBookedTickets(List<Ticket> bookedTickets) {this.bookedTickets = bookedTickets;}

    public List<Ticket> getHistory() { return History; }

    public void setHistory(List<Ticket> history) { History = history; }
}