package org.example.municipaltheater.models.RegisteredUsers;

import org.example.municipaltheater.models.ShowModels.Ticket;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class UserUpdateDTO {
    @Indexed(unique=true)
    @NotBlank(message= "Username is a required field.")
    private String username;
    @Indexed(unique=true)
    @NotBlank(message= "Username is a required field.")
    private String email;
    @NotBlank(message= "Password is a required field.")
    private String Password;

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

}
