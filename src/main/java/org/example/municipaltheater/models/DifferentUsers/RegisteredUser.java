package org.example.municipaltheater.models.DifferentUsers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@ToString
@Document(collection = "Registered Users")

public class RegisteredUser {
    @Id
    private String userID;
    private String Userame;
    private String Password;
    @Indexed(unique=true)
    private String PhoneNum;
    @Indexed(unique=true)
    private String email;

}

