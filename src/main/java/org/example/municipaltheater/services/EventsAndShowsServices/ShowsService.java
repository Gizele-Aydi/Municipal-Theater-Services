package org.example.municipaltheater.services.EventsAndShowsServices;

import jakarta.validation.ValidationException;
import org.example.municipaltheater.interfaces.ShowsInterfaces.ShowsHandlingInterface;
import org.example.municipaltheater.models.EventModels.Event;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.ShowsRepository;
import org.example.municipaltheater.utils.DefinedExceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShowsService implements ShowsHandlingInterface {

    @Autowired
    private ShowsRepository ShowRepo;

    public List<Show> findAllShows() {
        return ShowRepo.findAll();
    }

    public Show saveShow(Show show) {
        StringBuilder errorMessages = getStringBuilder(
                show.getShowName(),
                show.getShowDate(),
                show.getShowDuration(),
                show.getShowStartTime(),
                show.getShowPhotoURL()
        );
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages.toString().trim());
        }

        if (ShowRepo.existsByShowPhotoURL(show.getShowPhotoURL())) {
            throw new ValidationException("The show photo URL is already used by another show.");
        }

        if (ShowRepo.existsByShowNameAndShowDateAndShowStartTime(show.getShowName(), show.getShowDate(), show.getShowStartTime())) {
            throw new DefinedExceptions.OAlreadyExistsException("Such a show already exists.");
        }

        return ShowRepo.save(show);
    }

    public Optional<Show> findShowByID(String id) {
        return ShowRepo.findById(id);
    }

    public Show updateShow(String id, Show show) {
        StringBuilder errorMessages = getStringBuilder(
                show.getShowName(),
                show.getShowDate(),
                show.getShowDuration(),
                show.getShowStartTime(),
                show.getShowPhotoURL()
        );
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages.toString().trim());
        }
        if (show.getShowPhotoURL() != null && !show.getShowPhotoURL().equals("")) {
            Optional<Show> existingShowWithSamePhotoURL = ShowRepo.findByShowPhotoURL(show.getShowPhotoURL());
            if (existingShowWithSamePhotoURL.isPresent() && !existingShowWithSamePhotoURL.get().getShowID().equals(id)) {
                throw new ValidationException("The show photo URL is already in use by another show.");
            }
        }
        Optional<Show> showOptional = ShowRepo.findById(id);
        if (showOptional.isPresent()) {
            Show existingShow = showOptional.get();
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
                existingShow.setShowPhotoURL(show.getShowPhotoURL());
                isChanged = true;
            }
            if (!isChanged) {
                throw new ValidationException("There were no changes in the fields of the show.");
            }
            return ShowRepo.save(existingShow);
        } else {
            throw new DefinedExceptions.ONotFoundException("This show wasn't found, ID: " + id);
        }
    }

    public boolean deleteShowByID(String id) {
        if (ShowRepo.existsById(id)) {
            ShowRepo.deleteById(id);
            return true;
        } else {
            throw new DefinedExceptions.ONotFoundException("This show wasn't found, ID: " + id);
        }
    }

    public List<Show> searchShows(String searchQuery) {
        List<Show> results = ShowRepo.findByShowNameContainingIgnoreCase(searchQuery);
        return results;
    }

    private static StringBuilder getStringBuilder(
            String showName,
            Date showDate,
            String showDuration,
            LocalTime showStartTime,
            String showPhotoURL
    ) {
        StringBuilder errorMessages = new StringBuilder();
        if (showName == null || showName.trim().isEmpty()) {
            errorMessages.append("You have to enter the show's name. ");
        }
        if (showDate == null) {
            errorMessages.append("You have to enter the show's date. ");
        }
        if (showDuration == null || showDuration.trim().isEmpty()) {
            errorMessages.append("You have to enter the show's duration. ");
        }
        if (showStartTime == null) {
            errorMessages.append("You have to enter the show's starting time. ");
        }
        if (showPhotoURL == null || showPhotoURL.trim().isEmpty()) {
            errorMessages.append("You have to enter the show's photo URL. ");
        }
        return errorMessages;
    }
}
