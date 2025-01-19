package org.example.municipaltheater.models.Authentication.response;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "BlackListedTokens")

public class BlackListedToken {
    @Id
    private String id;
    private String token;
    private Date expiryDate;

    public BlackListedToken(String token, Date expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public String getToken() {
        return token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
}