package org.example.municipaltheater.models.ShowModels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "Tickets")

public class Ticket {
    @Id
    @Generated
    private String ticketID;
    @DBRef @JsonIgnore @NotBlank(message = "The ticket must be associated with a show.")
    private Show show;
    @NotBlank(message = "The ticket seat type shouldn't be empty.")
    private SeatType seat;
    @NotNull(message = "The ticket price shouldn't be empty.")
    private double price;
    @DBRef @JsonIgnore
    private RegisteredUser user;
    @NotNull(message = "The ticket must belong to a user.")
    private boolean isHistory;
    private boolean isPaid;

    public String getTicketID() {
        return ticketID;
    }
    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public Show getShow() {
        return show;
    }
    public void setShow(Show show) {
        this.show = show;
    }

    public SeatType getSeat() {
        return seat;
    }
    public void setSeat(SeatType seat) {
        this.seat = seat;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public RegisteredUser getUser() {
        return user;
    }
    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public boolean isHistory() {
        return isHistory;
    }
    public void setHistory(boolean history) {
        isHistory = history;
    }

    public boolean isPaid() {
        return isPaid;
    }
    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getShowName() {
        return show != null ? show.getShowName() : "Deleted Show";
    }
}
