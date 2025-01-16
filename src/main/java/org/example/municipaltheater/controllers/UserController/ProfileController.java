package org.example.municipaltheater.controllers.UserController;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.RegisteredUsers.*;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.example.municipaltheater.services.EventsAndShowsServices.ShowsService;
import org.example.municipaltheater.services.EventsAndShowsServices.TicketsService;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UsersService UserService;
    private final TicketsService TicketService;
    private final ShowsService ShowService;

    @Autowired
    public ProfileController(UsersService userService, TicketsService TicketService, ShowsService showService) {
        UserService = userService;
        this.TicketService = TicketService;
        ShowService = showService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/View")
    public ResponseEntity<APIResponse<RegisteredUser>> ViewProfile(@AuthenticationPrincipal String userId) {
        RegisteredUser user = UserService.findUserProfileById(userId).orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Your profile was successfully retrieved.", user));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/Update")
    public ResponseEntity<APIResponse<RegisteredUser>> UpdateProfile(@AuthenticationPrincipal String userId, @RequestBody RegisteredUser updatedUser) {
        RegisteredUser updated = UserService.findUserProfileById(userId).orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Your profile was successfully updated.", updated));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/Delete")
    public ResponseEntity<APIResponse<Void>> DeleteProfile(@AuthenticationPrincipal String userId) {
        UserService.deleteUserByID(userId);
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Your account was successfully deleted.", null));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/Booked-Tickets")
    public ResponseEntity<APIResponse<Map<String, Object>>> ViewBookedTickets(@AuthenticationPrincipal String userId) {
        RegisteredUser user = UserService.findUserProfileById(userId).orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        Map<String, Object> responseData = UserService.findBookedTicketsForUser(user);
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Currently booked tickets retrieved successfully.", responseData));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/Booked-Tickets/{ticket-id}/Cancel")
    public ResponseEntity<APIResponse<Void>> CancelTicket(@PathVariable String ticketID) {
        try {
            TicketService.deleteTicket(ticketID);
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Ticket cancelled and removed successfully.", null));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/Booked-Tickets/Pay")
    public ResponseEntity<APIResponse<String>> PayTickets(@PathVariable String id, @RequestParam String userID) {
        try {
            RegisteredUser user = UserService.findUserProfileById(userID).orElseThrow(() -> new ONotFoundException("This user wasn't found."));
            Show show = ShowService.findShowByID(id).orElseThrow(() -> new ONotFoundException("This show wasn't found."));
            Ticket ticket = TicketService.getTicketByShowAndUser(show, user);
            if (ticket == null) {
                throw new ONotFoundException("Ticket not found.");
            }
            TicketService.payTicketAndMoveToHistory(ticket.getTicketID(), user, show);
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The payment request can be processed successfully.", "Ticket ID: " + ticket.getTicketID()));
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse<>(HttpStatus.CONFLICT.value(), "The ticket has already been modified by another user.", null));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the payment.", null));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/History")
    public ResponseEntity<APIResponse<List<Map<String, Object>>>> ViewHistory(@AuthenticationPrincipal String userID) {
        RegisteredUser user = UserService.findUserProfileById(userID).orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        List<Map<String, Object>> filteredHistory = UserService.findBookingHistoryByUser(user);
        if (filteredHistory.isEmpty()) {
            return ResponseEntity.ok(new APIResponse<List<Map<String, Object>>>(HttpStatus.OK.value(), "No history of previous bookings found.", filteredHistory));
        }
        return ResponseEntity.ok(new APIResponse<List<Map<String, Object>>>(HttpStatus.OK.value(), "Previous ticket bookings retrieved successfully.", filteredHistory));
    }


}
