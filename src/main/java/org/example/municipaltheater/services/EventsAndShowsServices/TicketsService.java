package org.example.municipaltheater.services.EventsAndShowsServices;

import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.ShowModels.Seat;
import org.example.municipaltheater.models.ShowModels.SeatType;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.ShowsRepository;
import org.example.municipaltheater.repositories.TicketsRepository;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketsService {

    private final TicketsRepository TicketRepo;
    private final RegisteredUsersRepository UserRepo;
    private final ShowsRepository ShowRepo;

    @Autowired
    public TicketsService(TicketsRepository ticketRepo, RegisteredUsersRepository userRepo, ShowsRepository showRepo) {
        TicketRepo = ticketRepo;
        UserRepo = userRepo;
        ShowRepo = showRepo;
    }

    public Ticket addTicket(Show show, RegisteredUser user, SeatType seatType) {
        if (show == null || user == null) {
            throw new IllegalArgumentException("Show or User cannot be null.");
        }
        Seat seat = show.getSeats().stream().filter(s -> s.getSeatType().equals(seatType)).findFirst().orElseThrow(() -> new IllegalArgumentException("This seat type isn't available for this show."));
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
        return TicketRepo.findByShowAndUser(show, user).orElseThrow(() -> new ONotFoundException("No ticket found for this show and user."));
    }

    @Cacheable(value = "UserBookedTickets", key = "#userId + '-' + #pageable.pageNumber")
    public List<Ticket> getTicketsForUser(RegisteredUser user) {
        return TicketRepo.findByUser(user);
    }

    public void deleteTicket(String ticketID) {
        Ticket ticket = TicketRepo.findById(ticketID).orElseThrow(() -> new ONotFoundException("Ticket not found, ID: " + ticketID));
        RegisteredUser user = ticket.getUser();
        user.getBookedTickets().remove(ticket);
        UserRepo.save(user);
        Show show = ticket.getShow();
        SeatType seatType = ticket.getSeat();
        boolean seatsUpdated = show.increaseAvailableSeats(seatType);
        if (!seatsUpdated) {
            throw new IllegalStateException("Failed to update available seats.");
        }
        ShowRepo.save(show);
        TicketRepo.delete(ticket);
    }


    @Transactional
    public void payTicketAndMoveToHistory(String ticketID, RegisteredUser user, Show show) {
        Ticket ticket = TicketRepo.findById(ticketID).orElseThrow(() -> new RuntimeException("Ticket not found."));
        if (ticket.isHistory()) {
            throw new IllegalStateException("Ticket has already been processed.");
        }
        ticket.setHistory(true);
        ticket.getUser().getHistory().add(ticket);
        UserRepo.save(ticket.getUser());
        TicketRepo.save(ticket);
    }

}
