package org.example.municipaltheater.models.EventsAndShows;

import java.util.Date;
import java.util.List;

import lombok.*;
import org.example.municipaltheater.models.DifferentUsers.RegisteredUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@ToString
@Document(collection = "Shows")

public class Show {
    @Id
    private String ShowID;
    @NotBlank(message = "Show name is required.")
    private String ShowName;
    @NotNull(message = "Show date is required.")
    private Date ShowDate;
    private String ShowDescription;
    @DBRef(lazy = true)
    private List<RegisteredUser> BookedAudience;

    public Show () {}

    public String getShowID() {
        return ShowID;
    }

    public void setShowID(String showID) {
        ShowID = showID;
    }

    public String getShowName() {
        return ShowName;
    }

    public void setShowName(String showName) {
        ShowName = showName;
    }

    public Date getShowDate() {
        return ShowDate;
    }

    public void setShowDate(Date showDate) {
        ShowDate = showDate;
    }

    public String getShowDescription() {
        return ShowDescription;
    }

    public void setShowDescription(String showDescription) {
        ShowDescription = showDescription;
    }

    public List<RegisteredUser> getBookedAudience() {
        return BookedAudience;
    }

    public void setBookedAudience(List<RegisteredUser> bookedAudience) {
        BookedAudience = bookedAudience;
    }
}
