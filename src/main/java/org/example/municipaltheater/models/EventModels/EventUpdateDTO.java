package org.example.municipaltheater.models.EventModels;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.internal.connection.Time;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

public class EventUpdateDTO {

    @NotBlank(message = "Event name is required.")
    private String eventName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @NotNull(message = "Event date is required.")
    private Date eventDate;
    private String eventDuration;
    @NotNull(message = "Event starting time is required.")
    private LocalTime eventStartTime;
    @NotBlank(message = "Event description is required.")
    private String eventDescription;
    private String eventPhotoURL;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(String eventDuration) {
        this.eventDuration = eventDuration;
    }

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventPhotoURL() {
        return eventPhotoURL;
    }

    public void setEventPhotoURL(String eventPhotoURL) {
        this.eventPhotoURL = eventPhotoURL;
    }
}
