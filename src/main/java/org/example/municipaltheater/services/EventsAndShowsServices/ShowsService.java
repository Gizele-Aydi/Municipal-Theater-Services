package org.example.municipaltheater.services.EventsAndShowsServices;

import jakarta.validation.ValidationException;
import org.example.municipaltheater.interfaces.ShowsInterfaces.ShowsHandlingInterface;
import org.example.municipaltheater.models.ShowModels.Show;
import org.example.municipaltheater.repositories.ShowsAndEventsRepositories.ShowsRepository;
import org.example.municipaltheater.utils.DefinedExceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        StringBuilder errorMessages = getStringBuilder(show.getShowName(), show.getShowDate(), show.getShowDescription());
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages.toString().trim());
        }
        if (ShowRepo.existsByShowNameAndShowDate(show.getShowName(), show.getShowDate())) {
            throw new DefinedExceptions.OAlreadyExistsException("Such a show already exists.");
        }
        return ShowRepo.save(show);
    }

    public Optional<Show> findShowByID(String id) {
        return ShowRepo.findById(id);
    }

    public Show updateShow(String id, Show show) {
        StringBuilder errorMessages = getStringBuilder(show.getShowName(), show.getShowDate(), show.getShowDescription());
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages.toString().trim());
        }
        Optional<Show> showOptional = ShowRepo.findById(id);
        if (showOptional.isPresent()) {
            Show existingShow = showOptional.get();
            boolean isChanged = false;
            if (!existingShow.getShowName().equals(show.getShowName())) {
                isChanged = true;
            }
            if (!existingShow.getShowDate().equals(show.getShowDate())) {
                isChanged = true;
            }
            if (!existingShow.getShowDescription().equals(show.getShowDescription())) {
                isChanged = true;
            }
            if (!isChanged) {
                throw new ValidationException("There were no changes in the fields of the show.");
            }
            existingShow.setShowName(show.getShowName());
            existingShow.setShowDate(show.getShowDate());
            existingShow.setShowDescription(show.getShowDescription());
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

    private static StringBuilder getStringBuilder(String showName, Date showDate, String showDescription) {
        StringBuilder errorMessages = new StringBuilder();
        if (showName == null || showName.trim().isEmpty()) {
            errorMessages.append("You have to enter the show's name. ");
        }
        if (showDate == null) {
            errorMessages.append("You have to enter the show's date. ");
        }
        if (showDescription == null || showDescription.trim().isEmpty()) {
            errorMessages.append("You have to enter the show's description. ");
        }
        return errorMessages;
    }
}
