package org.example.municipaltheater.models.ShowModels;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.internal.connection.Time;
import org.example.municipaltheater.models.DifferentUsers.RegisteredUser;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "Shows")

public class ShowUpdateDTO {

    @NotBlank(message = "Show name is required.")
    private String showName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @NotNull(message = "Show date is required.")
    private Date showDate;
    @NotBlank(message = "Show duration is required.")
    private String showDuration;
    @NotNull(message = "Show starting time is required.")
    private LocalTime showStartTime;
    private String showPhotoURL;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public String getShowDuration() {
        return showDuration;
    }

    public void setShowDuration(String showDuration) {
        this.showDuration = showDuration;
    }

    public LocalTime getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(LocalTime showStartTime) {
        this.showStartTime = showStartTime;
    }

    public String getShowPhotoURL() {
        return showPhotoURL;
    }

    public void setShowPhotoURL(String showPhotoURL) {
        this.showPhotoURL = showPhotoURL;
    }
}
