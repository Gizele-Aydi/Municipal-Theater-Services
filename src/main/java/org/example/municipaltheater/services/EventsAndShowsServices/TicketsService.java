package org.example.municipaltheater.services.EventsAndShowsServices;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.ShowModels.Seat;
import org.example.municipaltheater.models.ShowModels.SeatType;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.repositories.TicketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketsService {

    private final TicketsRepository TicketRepo;
    private final RegisteredUsersRepository UserRepo;

    @Autowired
    public TicketsService(TicketsRepository ticketRepo, RegisteredUsersRepository userRepo) {
        TicketRepo = ticketRepo;
        UserRepo = userRepo;
    }

    public Ticket addTicket(Show show, RegisteredUser user, SeatType seatType) {
        if (show == null || user == null) {
            throw new IllegalArgumentException("Show or User cannot be null.");
        }
        Seat seat = show.getSeats().stream()
                .filter(s -> s.getSeatType().equals(seatType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Seat type not available for this show."));

        double seatPrice = seat.getPrice();
        Ticket ticket = new Ticket();
        ticket.setShow(show);
        ticket.setUser(user);
        ticket.setSeat(seatType);
        ticket.setPrice(seatPrice);
        ticket.setHistory(false);
        return TicketRepo.save(ticket);
    }

    public Ticket getTicketByShowAndUser(Show show, RegisteredUser user) {
        Optional<Ticket> ticketOptional = TicketRepo.findByShowAndUser(show, user);
        return ticketOptional.orElseThrow(() -> new RuntimeException("No ticket found for this user and show."));
    }

    public double calculateTotalAmount(Show show) {
        return show.getSeats().stream()
                .mapToDouble(Seat::getPrice)
                .sum();
    }

    public List<Ticket> getTicketsForShow(Show show) {
        return TicketRepo.findByShow(show);
    }

    public List<Ticket> getTicketsForUser(RegisteredUser user) {
        return TicketRepo.findByUser(user);
    }

    public Ticket updateTicket(String ticketID, SeatType newSeatType) {
        Ticket ticket = TicketRepo.findById(ticketID)
                .orElseThrow(() -> new RuntimeException("Ticket not found."));
        Show show = ticket.getShow();
        Seat newSeat = show.getSeats().stream()
                .filter(s -> s.getSeatType().equals(newSeatType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("New seat type not available."));

        ticket.setSeat(newSeatType);
        ticket.setPrice(newSeat.getPrice());
        return TicketRepo.save(ticket);
    }

    public Ticket markTicketAsHistory(String ticketID) {
        Ticket ticket = TicketRepo.findById(ticketID)
                .orElseThrow(() -> new RuntimeException("Ticket not found."));
        ticket.setHistory(true);
        return TicketRepo.save(ticket);
    }

    public void moveToHistory(Ticket ticket) {
        ticket.setHistory(true);
        ticket.getUser().getHistory().add(ticket);
        UserRepo.save(ticket.getUser());
    }

}
