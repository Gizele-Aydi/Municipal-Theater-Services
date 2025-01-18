package org.example.municipaltheater.services.EventsAndShowsServices;

import jakarta.validation.Valid;
import org.example.municipaltheater.interfaces.ShowsInterfaces.ShowsHandlingInterface;
import org.example.municipaltheater.models.RegisteredUsers.RegisteredUser;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.models.ShowModels.Ticket;
import org.example.municipaltheater.repositories.DifferentUsersRepositories.RegisteredUsersRepository;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.ShowsRepository;
import org.example.municipaltheater.repositories.TicketsRepository;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.example.municipaltheater.utils.DefinedExceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ShowsService implements ShowsHandlingInterface {

    private static final Logger logger = LoggerFactory.getLogger(ShowsService.class);
    private final ShowsRepository ShowRepo;
    private final TicketsRepository TicketRepo;
    private final TicketsService TicketService;
    private final UsersService UserService;

    @Autowired
    public ShowsService(ShowsRepository showRepo, TicketsRepository ticketRepo, TicketsService ticketService, UsersService userService) {
        ShowRepo = showRepo;
        TicketRepo = ticketRepo;
        TicketService = ticketService;
        UserService = userService;
    }

    public List<Show> findAllShows() {
        logger.info("Fetching all shows");
        return ShowRepo.findAll();
    }

    public Show saveShow(@Valid Show show) {
        logger.info("Attempting to save show: {}", show.getShowName());
        logger.info("Show Details: {}", show);
        List<String> missingFields = getMissingFields(show);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        if (ShowRepo.existsByShowName(show.getShowName())) {
            throw new OAlreadyExistsException("Such a show already exists.");
        }
        if (isShowPhotoURLInUse(show.getShowPhotoURL(), null)) {
            throw new OServiceException("The show photo URL is already in use by another show.");
        }
        if (show.getSeats() == null || show.getSeats().isEmpty()) {
            throw new OServiceException("Seats is/are empty");
        }
        return ShowRepo.save(show);
    }

    public Optional<Show> findShowByID(String id) {
        return ShowRepo.findById(id);
    }

    public Show updateShow(String id, @Valid Show show) {
        logger.info("Attempting to update show with ID: {}", id);
        logger.info("Show Details to update: {}", show);
        List<String> missingFields = getMissingFields(show);
        if (!missingFields.isEmpty()) {
            throw new OServiceException(String.join(" and ", missingFields) + " is/are empty");
        }
        Optional<Show> existingShowOpt = ShowRepo.findById(id);
        if (existingShowOpt.isEmpty()) {
            throw new ONotFoundException("This show wasn't found, ID: " + id);
        }
        Show existingShow = existingShowOpt.get();
        boolean isChanged = false;
        if (!existingShow.getShowName().equals(show.getShowName())) {
            existingShow.setShowName(show.getShowName());
            isChanged = true;
        }
        if (!existingShow.getShowDate().equals(show.getShowDate())) {
            existingShow.setShowDate(show.getShowDate());
            isChanged = true;
        }
        if (!existingShow.getShowDuration().equals(show.getShowDuration())) {
            existingShow.setShowDuration(show.getShowDuration());
            isChanged = true;
        }
        if (!existingShow.getShowStartTime().equals(show.getShowStartTime())) {
            existingShow.setShowStartTime(show.getShowStartTime());
            isChanged = true;
        }
        if (!existingShow.getShowPhotoURL().equals(show.getShowPhotoURL())) {
            if (isShowPhotoURLInUse(show.getShowPhotoURL(), id)) {
                throw new OServiceException("This photo URL is already used by another show.");
            }
            existingShow.setShowPhotoURL(show.getShowPhotoURL());
            isChanged = true;
        }
        if (!existingShow.getSeats().equals(show.getSeats())) {
            existingShow.setSeats(show.getSeats());
            isChanged = true;
        }
        if (!isChanged) {
            throw new OServiceException("There were no changes in the fields of the show.");
        }
        return ShowRepo.save(existingShow);
    }

    public boolean deleteShowByID(String id) {
        logger.info("Attempting to delete show with ID: {}", id);
        Show show = ShowRepo.findById(id).orElseThrow(() -> new ONotFoundException("This show wasn't found, ID: " + id));
        UserService.removeTicketsForDeletedShow(id);
        ShowRepo.delete(show);
        return true;
    }


    public List<Show> searchShows(String searchQuery) {
        logger.info("Searching shows with query: {}", searchQuery);
        return ShowRepo.findByShowNameContainingIgnoreCase(searchQuery);
    }

    private boolean isShowPhotoURLInUse(String showPhotoURL, String showId) {
        if (showPhotoURL != null && !showPhotoURL.trim().isEmpty()) {
            Show existingShow = ShowRepo.findByShowPhotoURL(showPhotoURL);
            return existingShow != null && !existingShow.getShowID().equals(showId);
        }
        return false;
    }

    private List<String> getMissingFields(Show show) {
        return Stream.of(
                show.getShowName() == null || show.getShowName().isEmpty() ? "Show Name" : null,
                show.getShowDate() == null ? "Show Date" : null,
                show.getShowDuration() == null || show.getShowDuration().isEmpty() ? "Show Duration" : null,
                show.getShowStartTime() == null ? "Show Start Time" : null,
                show.getShowPhotoURL() == null || show.getShowPhotoURL().trim().isEmpty() ? "Show Photo URL" : null,
                show.getSeats() == null || show.getSeats().isEmpty() ? "Seats" : null
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
