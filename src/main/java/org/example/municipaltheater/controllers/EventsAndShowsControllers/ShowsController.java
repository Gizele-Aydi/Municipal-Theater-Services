package org.example.municipaltheater.controllers.EventsAndShowsControllers;

import org.example.municipaltheater.models.APIResponse;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.ShowModels.*;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.ShowsRepository;
import org.example.municipaltheater.services.EventsAndShowsServices.ShowsService;
import org.example.municipaltheater.services.EventsAndShowsServices.TicketsService;
import org.example.municipaltheater.utils.DefinedExceptions.*;
import org.example.municipaltheater.interfaces.ShowsInterfaces.ShowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

@RestController
@RequestMapping("/Shows")
@CrossOrigin(origins = "*")
public class ShowsController {

    private final ShowsService ShowService;
    private final TicketsService TicketService;
    private final ShowMapper showMapper;
    private final RegisteredUsersRepository UserRepo;
    private final ShowsRepository ShowRepo;

    @Autowired
    public ShowsController(ShowsService ShowService, TicketsService TicketService, ShowMapper showMapper, RegisteredUsersRepository userRepo, ShowsRepository showRepo) {
        this.ShowService = ShowService;
        this.TicketService = TicketService;
        this.showMapper = showMapper;
        UserRepo = userRepo;
        ShowRepo = showRepo;
    }

    @GetMapping(value = "/All")
    public ResponseEntity<APIResponse<Show>> GetAllShows() {
        List<Show> shows = ShowService.findAllShows();
        if (shows.isEmpty()) {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The Shows list is empty", null));
        } else {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Shows retrieved successfully", shows));
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<APIResponse<ShowUpdateDTO>> GetShow(@PathVariable String id) {
        return ShowService.findShowByID(id)
                .map(show -> {
                    ShowUpdateDTO showDTO = showMapper.ShowToShowUpdateDTO(show);
                    return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Show found.", showDTO));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), "This show wasn't found, ID: " + id, null)));
    }

    @PostMapping(value = "/Add")
    public ResponseEntity<APIResponse<Show>> AddShow(@RequestBody @Valid ShowUpdateDTO showUpdateDTO) {
        try {
            Show show = showMapper.ShowUpdateDTOToShow(showUpdateDTO);
            Show addedShow = ShowService.saveShow(show);
            return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>(HttpStatus.CREATED.value(), "Show added successfully", addedShow));
        } catch (OAlreadyExistsException | OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<APIResponse<Show>> UpdateShow(@PathVariable String id, @RequestBody ShowUpdateDTO showUpdateDTO) {
        try {
            Show show = ShowService.findShowByID(id).orElseThrow(() ->
                    new ONotFoundException("This show wasn't found, ID: " + id));

            showMapper.updateShowFromDTO(showUpdateDTO, show);
            Show updatedShow = ShowService.updateShow(id, show);
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "The Show was updated successfully.", updatedShow));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (OServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @DeleteMapping(value = "/Delete/{id}")
    public ResponseEntity<APIResponse<Void>> DeleteShow(@PathVariable String id) {
        try {
            boolean isDeleted = ShowService.deleteShowByID(id);
            if (isDeleted) {
                return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Show deleted successfully.", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), "This show wasn't found, ID: " + id, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @PostMapping("/Search")
    public ResponseEntity<APIResponse<SearchQuery>> SearchShows(@RequestBody Map<String, String> request) {
        String query = request.get("searchQuery");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Search query cannot be empty.", null));
        }
        List<Show> matchingShows = ShowService.searchShows(query);
        if (matchingShows.isEmpty()) {
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "No shows found for your search.", null));
        }
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Shows found.", new SearchQuery(matchingShows)));
    }

    //@PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/Book")
    public ResponseEntity<APIResponse<String>> BookTicket(@PathVariable String id, @RequestParam String userID, @RequestParam SeatType seatType) {
        try {
            RegisteredUser user = UserRepo.findByUserID(userID).orElseThrow(() -> new ONotFoundException("This user wasn't found."));
            Show show = ShowRepo.findByShowID(id).orElseThrow(() -> new ONotFoundException("This show wasn't found."));
            boolean seatReduced = show.reduceAvailableSeats(seatType);
            if (!seatReduced) {
                throw new IllegalArgumentException("There are no longer seats available for the selected seat type.");
            }
            ShowRepo.save(show);
            Ticket ticket = TicketService.addTicket(show, user, seatType);
            if (user.getBookedTickets() == null) {
                user.setBookedTickets(new ArrayList<>());
            }
            user.getBookedTickets().add(ticket);
            UserRepo.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>(HttpStatus.CREATED.value(), "Ticket booked successfully!", "Ticket ID: " + ticket.getTicketID()));
        } catch (ONotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),"An error occurred while booking the ticket.", null));
        }
    }
}
