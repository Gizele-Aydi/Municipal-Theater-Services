package org.example.municipaltheater.controllers.UserController;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.RegisteredUsers.*;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.security.services.UserDetailsImpl;
import org.example.municipaltheater.services.EventsAndShowsServices.TicketsService;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.example.municipaltheater.utils.DefinedExceptions.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UsersService UserService;
    private final TicketsService TicketService;
    private final RegisteredUsersRepository UserRepo;

    @Autowired
    public ProfileController(UsersService userService, TicketsService TicketService, RegisteredUsersRepository userRepo) {
        UserService = userService;
        this.TicketService = TicketService;
        UserRepo = userRepo;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/view")
    public ResponseEntity<APIResponse<Map<String, String>>> viewProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        RegisteredUser user = UserService.findUserProfileById(userDetails.getUserID()).orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("userId", user.getUserID());
        userProfile.put("username", user.getUsername());
        userProfile.put("role", user.getRole().toString());
        userProfile.put("email", user.getEmail());
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(),"Your profile was successfully retrieved.", userProfile));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update")
    public ResponseEntity<APIResponse<RegisteredUser>> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RegisteredUser updatedUser) {
        updatedUser.setRole(null);
        updatedUser.setBookedTickets(null);
        updatedUser.setHistory(null);
        RegisteredUser updated = UserService.updateUser(userDetails.getUserID(), updatedUser);
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Your profile was successfully updated.", updated
        ));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/delete")
    public ResponseEntity<APIResponse<Void>> deleteProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserService.deleteUserByID(userDetails.getUserID());
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Your account was successfully deleted.", null));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/booked-tickets")
    public ResponseEntity<APIResponse<Map<String, Object>>> viewBookedTickets(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        RegisteredUser user = UserService.findUserProfileById(userDetails.getUserID()).orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        Map<String, Object> responseData = UserService.findBookedTicketsForUser(user);
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(),"Currently booked tickets retrieved successfully.", responseData));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/booked-tickets/{ticketID}/cancel")
    public ResponseEntity<APIResponse<Void>> cancelTicket(@PathVariable("ticketID") String ticketID) {
        try {
            TicketService.deleteTicket(ticketID);
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Ticket cancelled and removed successfully.", null));
        } catch (ONotFoundException e)  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/booked-tickets/pay")
    public ResponseEntity<APIResponse<String>> payTickets(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            RegisteredUser user = UserService.findUserProfileById(userDetails.getUserID()).orElseThrow(() -> new ONotFoundException("User not found."));
            List<Ticket> ticketsToProcess = new ArrayList<>(user.getBookedTickets());
            for (Ticket ticket : ticketsToProcess) {
                TicketService.payTicketAndMoveToHistory(ticket.getTicketID(), user, ticket.getShow());
            }
            user.getBookedTickets().clear();
            user = UserRepo.save(user);
            if (!user.getBookedTickets().isEmpty()) {
                throw new RuntimeException("Failed to clear booked tickets");
            }
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "All tickets have been paid successfully and moved to history.", null));
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse<>(HttpStatus.CONFLICT.value(), "One or more tickets have been modified by another user.", null));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the payment: " + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/history")
    public ResponseEntity<APIResponse<List<Map<String, Object>>>> viewHistory(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        RegisteredUser user = UserService.findUserProfileById(userDetails.getUserID()).orElseThrow(() -> new ONotFoundException("Your profile can't seem to be found."));
        List<Map<String, Object>> filteredHistory = UserService.findBookingHistoryByUser(user);
        APIResponse<List<Map<String, Object>>> response;
        if (filteredHistory.isEmpty()) {
            response = new APIResponse<List<Map<String, Object>>>(HttpStatus.OK.value(),"No history of previous bookings found.",filteredHistory);
        } else {
            response = new APIResponse<List<Map<String, Object>>>(HttpStatus.OK.value(),"Previous ticket bookings retrieved successfully.",filteredHistory);
        }
        return ResponseEntity.ok(response);
    }
}
