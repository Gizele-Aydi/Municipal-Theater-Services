package org.example.municipaltheater.models.ShowModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "Shows")

public class Show {
    @Id
    @Generated
    private String showID;
    @NotBlank(message = "Show name is required.")
    private String showName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @NotNull(message = "Show date is required.")
    private Date showDate;
    @NotBlank(message = "Show duration is required.")
    private String showDuration;
    @NotNull(message = "Show starting time is required.")
    private LocalTime showStartTime;
    @NotBlank(message = "Show photo is required.")
    private String showPhotoURL;
    @JsonProperty("seats")
    private List<Seat> seats;
    @DBRef(lazy = true)
    private List<Ticket> tickets = new ArrayList<>();

    public String getShowID() {
        return showID;
    }
    public void setShowID(String showID) {
        this.showID = showID;
    }

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

    public List<Seat> getSeats() {
        return seats;
    }
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public boolean reduceAvailableSeats(SeatType seatType) {
        for (Seat seat : seats) {
            if (seat.getSeatType().equals(seatType)) {
                if (seat.getAvailableSeats() > 0) {
                    seat.setAvailableSeats(seat.getAvailableSeats() - 1);
                    return true;
                } else {
                    return false;
                }
            }
        }
        throw new IllegalArgumentException("Seat type not found.");
    }

    public boolean increaseAvailableSeats(SeatType seatType) {
        for (Seat seat : seats) {
            if (seat.getSeatType().equals(seatType)) {
                seat.setAvailableSeats(seat.getAvailableSeats() + 1);
                return true;
            }
        }
        throw new IllegalArgumentException("Seat type not found.");
    }

}
