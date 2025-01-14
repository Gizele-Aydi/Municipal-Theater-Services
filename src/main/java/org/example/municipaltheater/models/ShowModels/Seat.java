package org.example.municipaltheater.models.ShowModels;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Seat {
    @NotBlank(message = "Seat type should be included.")
    private SeatType seatType;
    @NotNull(message = "The price of this seat type should be added.")
    private double price;
    @NotNull(message = "The number of available seats should be present.")
    private int availableSeats;

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

}