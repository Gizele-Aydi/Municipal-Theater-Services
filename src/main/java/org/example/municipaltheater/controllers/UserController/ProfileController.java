package org.example.municipaltheater.controllers.UserController;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.RegisteredUsers.*;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.example.municipaltheater.services.EventsAndShowsServices.TicketsService;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.example.municipaltheater.utils.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UsersService UserService;
    private final TicketsService TicketService;

    @Autowired
    public ProfileController(UsersService userService, TicketsService TicketService) {
        UserService = userService;
        this.TicketService = TicketService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/View")
    public ResponseEntity<APIResponse<RegisteredUser>> viewProfile(@AuthenticationPrincipal String userId) {
        RegisteredUser user = UserService.findUserById(userId)
                .orElseThrow(() -> new ONotFoundException("Your profile wasn't found."));
        return ResponseGenerator.Response(HttpStatus.OK, "You profile was successfully retrieved.", user);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/Update")
    public ResponseEntity<APIResponse<RegisteredUser>> updateProfile(@AuthenticationPrincipal String userId, @RequestBody RegisteredUser updatedUser) {
        RegisteredUser updated = UserService.updateUser(userId, updatedUser);
        return ResponseGenerator.Response(HttpStatus.OK, "You profile was successfully updated .", updated);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/Delete")
    public ResponseEntity<APIResponse<Void>> deleteProfile(@AuthenticationPrincipal String userId) {
        UserService.deleteUserByID(userId);
        return ResponseGenerator.Response(HttpStatus.OK, "Your account was successfully deleted .", null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/Booked-Tickets")
    public ResponseEntity<APIResponse<List<Ticket>>> viewBookedTickets(@AuthenticationPrincipal String userId) {
        RegisteredUser user = UserService.findUserById(userId)
                .orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));

        List<Ticket> bookedTickets = TicketService.getTicketsForUser(user);
        if (bookedTickets.isEmpty()) {
            return ResponseGenerator.Response(HttpStatus.OK, "No currently booked tickets found.", null);
        }
        return ResponseGenerator.Response(HttpStatus.OK, "Currently booked tickets retrieved successfully.", bookedTickets);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/History")
    public ResponseEntity<APIResponse<List<Ticket>>> viewHistory(@AuthenticationPrincipal String userId) {
        RegisteredUser user = UserService.findUserById(userId)
                .orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        List<Ticket> history = user.getHistory();
        if (history.isEmpty()) {
            return ResponseGenerator.Response(HttpStatus.OK, "No history of previous bookings found.", null);
        }
        return ResponseGenerator.Response(HttpStatus.OK, "Previous ticket bookings retrieved successfully.", history);
    }
}

